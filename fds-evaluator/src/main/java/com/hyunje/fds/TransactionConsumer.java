package com.hyunje.fds;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunje.fds.log.*;
import com.hyunje.fds.redis.RedisClient;
import com.hyunje.fds.serdes.JSONSerde;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class TransactionConsumer implements Runnable {
    public static final Logger logger = LogManager.getLogger(TransactionConsumer.class);
    private final String DEPOSIT_SRC_TOPIC = Constants.DEPOSIT_LOG_STREAM_SRC_TOPIC;
    private final String WITHDRAW_SRC_TOPIC = Constants.WITHDRAW_LOG_STREAM_SRC_TOPIC;
    private final String TRANSFER_SRC_TOPIC = Constants.TRANSFER_LOG_STREAM_SRC_TOPIC;
    private final String CONSUMER_GROUP = "FDS_TRANSACTION_CONSUMER";
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RedisClient redisClient = new RedisClient(Constants.REDIS_SERVER, Constants.REDIS_PORT);

    @Override
    public void run() {

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JSONSerde.class);
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        Consumer<String, FinanceTransactionLog> transactionConsumer = new KafkaConsumer<>(consumerProps);
        transactionConsumer.subscribe(Collections.singleton(Constants.RAW_LOG_GENERATOR_TOPIC));

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerde.class);

        Producer<String, DepositLog> depositLogProducer = new KafkaProducer<>(producerProps);
        Producer<String, WithdrawLog> withdrawLogProducer = new KafkaProducer<>(producerProps);
        Producer<String, TransferLog> transferLogProducer = new KafkaProducer<>(producerProps);

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Starting exit " + this.getClass().getSimpleName() + "...");
            transactionConsumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        try {
            while (true) {
                ConsumerRecords<String, FinanceTransactionLog> records = transactionConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, FinanceTransactionLog> record : records) {
                    String logType = record.key();
                    String jsonString = record.value().getJsonString();
                    logger.info(String.format("TransactionConsumer got %s log", logType));
                    switch (logType) {
                        case "register":
                            RegisterLog regLog = OBJECT_MAPPER.readValue(jsonString, RegisterLog.class);
                            redisClient.setRegisterUserLog(regLog);
                            logger.info("Saved register log to redis");
                            break;
                        case "create-account":
                            CreateAccountLog createAccLog = OBJECT_MAPPER.readValue(jsonString, CreateAccountLog.class);
                            redisClient.setCreateAccountLog(createAccLog);
                            logger.info("Saved create account log to redis");
                            break;
                        case "deposit":
                            DepositLog depositLog = OBJECT_MAPPER.readValue(jsonString, DepositLog.class);
                            ProducerRecord<String, DepositLog> depositRecord = new ProducerRecord<>(DEPOSIT_SRC_TOPIC, depositLog.getAccountId(), depositLog);
                            depositLogProducer.send(depositRecord);
                            logger.info(String.format("Send deposit log to topic %s\n", DEPOSIT_SRC_TOPIC));
                            break;
                        case "withdraw":
                            WithdrawLog withdrawLog = OBJECT_MAPPER.readValue(jsonString, WithdrawLog.class);
                            ProducerRecord<String, WithdrawLog> withdrawRecord = new ProducerRecord<>(WITHDRAW_SRC_TOPIC, withdrawLog.getAccountId(), withdrawLog);
                            withdrawLogProducer.send(withdrawRecord);
                            logger.info(String.format("Send withdraw log to topic %s\n", WITHDRAW_SRC_TOPIC));
                            break;
                        case "transfer":
                            TransferLog transferLog = OBJECT_MAPPER.readValue(jsonString, TransferLog.class);
                            ProducerRecord<String, TransferLog> transferRecord = new ProducerRecord<>(TRANSFER_SRC_TOPIC, transferLog.getAccountId(), transferLog);
                            transferLogProducer.send(transferRecord);
                            logger.info(String.format("Send transfer log to topic %s\n", TRANSFER_SRC_TOPIC));
                            break;
                        default:
                            logger.error("Wrong type of log");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WakeupException wakeupException) {

        } finally {
            logger.info(this.getClass().getSimpleName() + " is trying to close!");
            depositLogProducer.close();
            withdrawLogProducer.close();
            transferLogProducer.close();

            transactionConsumer.commitSync();
            transactionConsumer.close();
            logger.info("Closed " + this.getClass().getSimpleName());
        }
    }
}
