package com.hyunje.fds;

import com.hyunje.fds.log.Constants;
import com.hyunje.fds.log.TransactionStreamLog;
import com.hyunje.fds.redis.RedisClient;
import com.hyunje.fds.serdes.JSONSerde;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class TransactionEvaluator implements Runnable {
    private final RedisClient redisClient = new RedisClient(Constants.REDIS_SERVER, Constants.REDIS_PORT);

    private final DateTimeFormatter birthDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final DateTimeFormatter fDateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final LocalDate nowDate = LocalDate.now();

    private final int AGE_FILTER = 60;
    private final int CREATED_ACCOUNT_WITHIN_HOUR = 48;
    private final int LOW_BALANCE_WITHIN_HOUR = 2;
    private final int DEPOSIT_TOTAL_THRESHOLD = 100;
    private final int FRAUD_DETECT_THRESHOLD = 1;
    private final JSONSerde<TransactionStreamLog> transLogSerde = new JSONSerde<>();
    private final Serde<TransactionStreamLog> streamLogSerde = Serdes.serdeFrom(transLogSerde, transLogSerde);


    @Override
    public void run() {
        Properties evaluatorProp = new Properties();
        evaluatorProp.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-test");
        evaluatorProp.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // KStream 정의, Key: accountId
        KStream<String, TransactionStreamLog> depositStream = streamsBuilder.stream(Constants.DEPOSIT_LOG_STREAM_TOPIC,
                Consumed.with(Serdes.String(), streamLogSerde));
        depositStream.foreach(
                (key, value) -> System.out.println("AccId: " + key + " Deposited: " + value.getTransactionAmount() + " At " + value.getTimestamp())
        );

        KStream<String, TransactionStreamLog> withdrawAndTransferStream = streamsBuilder.stream(Constants.WITHDRAW_AND_TRANSFER_LOG_STREAM_TOPIC,
                Consumed.with(Serdes.String(), streamLogSerde));

        withdrawAndTransferStream.foreach(
                (key, value) -> System.out.println("AccId: " + key + " Transferred: " + value.getTransactionAmount() + " At " + value.getTimestamp())
        );

        KStream<String, TransactionStreamLog> groupedDeposit = depositStream
                .filter((key, value) -> {
                    System.out.println("\nFilter by user age, " + key + ", " + value);
                    String userId = redisClient.getCreatedAccountInfo(value.getAccountId(), "userId");
                    String birth = redisClient.getRegisteredUserInfo(userId, "birthDate");
                    LocalDate birthDate = LocalDate.parse(birth, birthDateFormatter);
                    long age = ChronoUnit.YEARS.between(birthDate, nowDate);
                    return age >= AGE_FILTER;
                })
                .filter((key, value) -> {
                    System.out.println("\nFilter by created account time, " + key + ", " + value);
                    String accCreated = redisClient.getCreatedAccountInfo(key, "tradeTime");

                    LocalDateTime createdTime = LocalDateTime.parse(accCreated, fDateFormatter);
                    LocalDateTime transactionTime = LocalDateTime.parse(value.getTimestamp(), fDateFormatter);

                    long created = ChronoUnit.HOURS.between(createdTime, transactionTime);
                    boolean div = created <= CREATED_ACCOUNT_WITHIN_HOUR;
                    System.out.println("created: " + created + " div: " + div + "\n");
                    return div;
                })
                .groupByKey()
                .windowedBy(SessionWindows.with(Duration.ofHours(CREATED_ACCOUNT_WITHIN_HOUR)))
                // window를 수행하기 전에 filter로 48시간 이후 입금 기록은 제외 했기 때문에, 48 시간 session window만 적용하면, 48시간 이내 입금한 기록만 남을 것.
                .reduce((aggLog, newLog) -> {
                    int totalAmount = aggLog.getTransactionAmount() + newLog.getTransactionAmount();
                    System.out.println("Reduce!, total amount: " + totalAmount + "\n");

                    TransactionStreamLog reducedLog = new TransactionStreamLog();
                    reducedLog.setTransactionAmount(totalAmount);
                    reducedLog.setAccountId(aggLog.getAccountId());
                    reducedLog.setTimestamp(newLog.getTimestamp());
                    return reducedLog;
                })
                // 최종 결과: 48시간 이내의 총 입금액과, 계좌 아이디, 100만원을 넘긴 최종 입금 시간, 합이 100만원 이하인 경우에는 자동으로 제외되기 때문에 timestamp는 체크하지 않아도 됨.
                .filter(((key, value) -> {
                    System.out.println("total deposited amount: " + value.getTransactionAmount());
                    return value.getTransactionAmount() >= DEPOSIT_TOTAL_THRESHOLD;
                }))
                .toStream()
                .map(((key, value) -> {
                    System.out.println("\nCHANGE key and value in deposit");
                    System.out.println(value + "\n");
                    return KeyValue.pair(key.key(), value);
                }));

        KStream<String, TransactionStreamLog> groupedWithdrawAndTransfer = withdrawAndTransferStream
                .groupByKey(Grouped.with(Serdes.String(), streamLogSerde))
                .windowedBy(SessionWindows.with(Duration.ofHours(LOW_BALANCE_WITHIN_HOUR)))
                .reduce((aggLog, newLog) -> {
                    int totalAmount = aggLog.getTransactionAmount() + newLog.getTransactionAmount();
                    System.out.println("Reduce in transferred, total: " + totalAmount);
                    TransactionStreamLog reducedLog = new TransactionStreamLog();
                    reducedLog.setTransactionAmount(totalAmount);
                    reducedLog.setAccountId(aggLog.getAccountId());
                    reducedLog.setTimestamp(newLog.getTimestamp());
                    System.out.println("Reduce " + reducedLog + "\n");
                    return reducedLog;
                })
                .toStream()
                .map((key, value) -> {
                    System.out.println("\nCHANGE key and value in transfer");
                    System.out.println(value + "\n");
                    return KeyValue.pair(key.key(), value);
                });


        groupedDeposit.foreach(
                ((key, value) -> {
                    System.out.println("--groupedDeposit--");
                    System.out.println("Key: " + key);
                    System.out.println("Value: " + value);
                    System.out.println("------------------");
                })
        );
        groupedWithdrawAndTransfer.foreach(
                ((key, value) -> {
                    System.out.println("--groupedWithdrawAndTransfer--");
                    System.out.println("Key: " + key);
                    System.out.println("Value: " + value);
                    System.out.println("------------------");
                })
        );

        KStream<String, Integer> detectStream = groupedDeposit
                .join(groupedWithdrawAndTransfer,
                        (deposit, transfer) -> {
                            System.out.println("JOIN deposit: " + deposit + ", transfer: " + transfer);
                            return deposit.getTransactionAmount() + transfer.getTransactionAmount();
                        },
                        JoinWindows.of(Duration.ofHours(2)),
                        Joined.with(
                                Serdes.String(),
                                streamLogSerde,
                                streamLogSerde
                        ));

        detectStream
                .foreach(
                        (accId, balance) -> {
                            if(balance <= FRAUD_DETECT_THRESHOLD)
                                System.out.printf("ACCOUNT ID %s DETECTED AS ABNORMAL!\n", accId);
                            else
                                System.out.printf("ACCOUNT ID %s IS NORMAL!\n", accId);
                        }
                );

        detectStream
                .filter(
                        (accId, balance) -> balance <= FRAUD_DETECT_THRESHOLD)
                .mapValues((balance) -> 1)
                .to(Constants.DETECTED_FRAUD_TOPIC, Produced.with(Serdes.String(), Serdes.Integer()));

        KafkaStreams evaluatorStreams = new KafkaStreams(streamsBuilder.build(), evaluatorProp);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-pipe-shutdown-hook") {
            @Override
            public void run() {
                evaluatorStreams.close();
                latch.countDown();
            }
        });

        try {
            evaluatorStreams.start();
            latch.await();
        } catch (WakeupException | InterruptedException wakeupException) {

        } finally {
            System.out.println(this.getClass().getSimpleName() + " is trying to close!");
            evaluatorStreams.close();
            System.out.println("Closed " + this.getClass().getSimpleName());
        }
    }
}
