package com.hyunje.fds.streams;

import com.hyunje.fds.log.Constants;
import com.hyunje.fds.log.TransactionStreamLog;
import com.hyunje.fds.log.TransferLog;
import com.hyunje.fds.serdes.JSONSerde;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class TransferStreamGenerator implements Runnable {

    @Override
    public void run() {
        Properties srcConsumerProps = new Properties();
        srcConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
        srcConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, Constants.TRANSFER_LOG_STREAM_SRC_CONSUMER_GROUP);
        srcConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        srcConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JSONSerde.class);
        srcConsumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        Consumer<String, TransferLog> transferConsumer = new KafkaConsumer<>(srcConsumerProps);
        transferConsumer.subscribe(Collections.singletonList(Constants.TRANSFER_LOG_STREAM_SRC_TOPIC));

        Properties streamProducerProps = new Properties();
        streamProducerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);
        streamProducerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        streamProducerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerde.class);

        Producer<String, TransactionStreamLog> streamLogProducer = new KafkaProducer<>(streamProducerProps);

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Starting exit " + this.getClass().getSimpleName() +"...");;
            transferConsumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        try {
            while (true) {
                ConsumerRecords<String, TransferLog> records = transferConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, TransferLog> record : records) {
                    String accId = record.key();
                    TransferLog log = record.value();
                    TransactionStreamLog streamLog = new TransactionStreamLog();
                    streamLog.setAccountId(accId);
                    streamLog.setTimestamp(log.getTradeTime());
                    streamLog.setTransactionAmount(log.getAmount() * -1);
                    System.out.printf("Received record of DepositLog, %s, %d, %s", accId, log.getAmount(), log.getTradeTime());

                    ProducerRecord<String, TransactionStreamLog> streamRecord = new ProducerRecord<>(Constants.TRANSFER_LOG_STREAM_TOPIC, accId, streamLog);

                    streamLogProducer.send(streamRecord);
                    System.out.println(", and sent to " + Constants.TRANSFER_LOG_STREAM_TOPIC + " amount: " + streamLog.getTransactionAmount());
                }
            }
        } catch (WakeupException wakeupException) {

        } finally {
            System.out.println(this.getClass().getSimpleName() + " is trying to close!");
            streamLogProducer.close();
            transferConsumer.commitSync();
            transferConsumer.close();
            System.out.println("Closed " + this.getClass().getSimpleName());
        }
    }
}
