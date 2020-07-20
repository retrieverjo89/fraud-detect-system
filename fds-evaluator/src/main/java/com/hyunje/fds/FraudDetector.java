package com.hyunje.fds;

import com.hyunje.fds.log.Constants;
import com.hyunje.fds.log.CreateAccountLog;
import com.hyunje.fds.redis.RedisClient;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class FraudDetector implements Runnable {
    private final String RESULT_TOPIC = Constants.DETECTED_FRAUD_TOPIC;
    private final String CONSUMER_GROUP = "FDS_DETECTION_CONSUMER";
    private final RedisClient redisClient = new RedisClient(Constants.REDIS_SERVER, Constants.REDIS_PORT);

    @Override
    public void run() {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        Consumer<String, Integer> resultConsumer = new KafkaConsumer<>(consumerProps);
        resultConsumer.subscribe(Collections.singleton(RESULT_TOPIC));

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Starting exit " + this.getClass().getSimpleName() + "...");

            resultConsumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        try {
            while (true) {
                ConsumerRecords<String, Integer> records = resultConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, Integer> record : records) {
                    String accId = record.key();
                    CreateAccountLog userInfo = redisClient.getCreatedAccountInfo(accId);
                    System.out.printf("User %s's account %s was detected as FRAUD!\n", userInfo.getUserId(), accId);
                }
            }
        } catch (WakeupException wakeupException) {

        } finally {
            System.out.println(this.getClass().getSimpleName() + " is trying to close!");
            resultConsumer.commitSync();
            resultConsumer.close();
            System.out.println("Closed " + this.getClass().getSimpleName());
        }

    }
}
