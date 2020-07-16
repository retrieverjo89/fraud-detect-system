package com.hyunje.fds;

import com.hyunje.fds.log.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.IntegerDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


public class TransactionEvaluator {
    private final static String TOPIC = "fds.test";
    private final static String CONSUMER_GROUP = "FDS_TEST_CONSUMER";
    private final static String BOOTSTRAP_SERVER = "192.168.0.2:9092";

    public static void main(String[] args) {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JSONSerde.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        KafkaConsumer<Integer, FinanceTransactionLog> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(TOPIC));

        while (true) {
            ConsumerRecords<Integer, FinanceTransactionLog> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<Integer, FinanceTransactionLog> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value().getJsonString());
            };
        }

    }
}
