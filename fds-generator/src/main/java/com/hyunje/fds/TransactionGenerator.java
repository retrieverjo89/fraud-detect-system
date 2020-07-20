package com.hyunje.fds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunje.fds.log.Constants;
import com.hyunje.fds.log.FinanceTransactionLog;
import com.hyunje.fds.serdes.JSONSerde;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class TransactionGenerator implements Runnable {
    private final static String BOOTSTRAP_SERVER = "172.16.40.3:9092";

    private static void sendRecord(Producer<String, FinanceTransactionLog> producer, List<ProducerRecord<String, FinanceTransactionLog>> recordList) throws InterruptedException, ExecutionException {
        for (ProducerRecord<String, FinanceTransactionLog> record : recordList) {
            long time = System.currentTimeMillis();
            RecordMetadata metadata = producer.send(record).get();
            long elapsedTime = System.currentTimeMillis() - time;
            System.out.printf("sent record(key=%s value=%s) " +
                            "meta(partition=%d, offset=%d) time=%d\n",
                    record.key(), record.value(), metadata.partition(),
                    metadata.offset(), elapsedTime);
        }
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerde.class);

        LogMaker logMaker = new LogMaker(Constants.RAW_LOG_GENERATOR_TOPIC);
        Producer<String, FinanceTransactionLog> producer = new KafkaProducer<>(props);

        try {
            List<ProducerRecord<String, FinanceTransactionLog>> fraudRecordList = logMaker.makeFraudLogs();
            sendRecord(producer, fraudRecordList);
            Thread.sleep(1000);

            List<ProducerRecord<String, FinanceTransactionLog>> nonFraudRecordList = logMaker.makeNonFraudLogs();
            sendRecord(producer, nonFraudRecordList);
        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
            e.printStackTrace();
        }
        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Starting exit " + this.getClass().getSimpleName() +"...");;
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }
}
