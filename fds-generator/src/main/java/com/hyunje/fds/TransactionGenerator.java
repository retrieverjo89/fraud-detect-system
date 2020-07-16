package com.hyunje.fds;

import com.hyunje.fds.log.FinanceTransactionLog;
import com.hyunje.fds.log.LogMaker;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;

/**
 * Hello world!
 */
public class TransactionGenerator {
    private final static String TOPIC = "fds.test";
    private final static String BOOTSTRAP_SERVER = "192.168.0.2:9092";

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerde.class);

        Producer<Integer, FinanceTransactionLog> producer = new KafkaProducer<>(props);
//        LogMaker logMaker = new LogMaker();

        FinanceTransactionLog rawLog = new FinanceTransactionLog();
        rawLog.setJsonString("json-string");
        rawLog.setLogType("create-account");

        for (int i = 0; i < 3; i++) {
            ProducerRecord<Integer, FinanceTransactionLog> record = new ProducerRecord<>(TOPIC, null, rawLog);

            long time = System.currentTimeMillis();
            RecordMetadata metadata = producer.send(record).get();
            long elapsedTime = System.currentTimeMillis() - time;
            System.out.printf("sent record(key=%s value=%s) " +
                            "meta(partition=%d, offset=%d) time=%d\n",
                    record.key(), record.value(), metadata.partition(),
                    metadata.offset(), elapsedTime);

            TimeUnit.SECONDS.sleep(1);

        }


    }
}
