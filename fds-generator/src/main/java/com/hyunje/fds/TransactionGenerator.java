package com.hyunje.fds;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hyunje.fds.log.Constants;
import com.hyunje.fds.log.FinanceTransactionLog;
import com.hyunje.fds.serdes.JSONSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class TransactionGenerator implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(TransactionGenerator.class.getName());

    private static void sendRecord(Producer<String, FinanceTransactionLog> producer, List<ProducerRecord<String, FinanceTransactionLog>> recordList) throws InterruptedException, ExecutionException {
        for (ProducerRecord<String, FinanceTransactionLog> record : recordList) {
            producer.send(record).get();
            logger.info(String.format("Generate log type: %s, json: %s", record.value().getLogType(), record.value().getJsonString()));
        }
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
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
            logger.info("Starting exit " + this.getClass().getSimpleName() + "...");
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }
}
