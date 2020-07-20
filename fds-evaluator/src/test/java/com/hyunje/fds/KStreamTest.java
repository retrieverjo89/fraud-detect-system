package com.hyunje.fds;

import com.hyunje.fds.log.Constants;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.Properties;

public class KStreamTest {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVER);

        StreamsBuilder builder = new StreamsBuilder();

        Serde<String> stringSerde = Serdes.String();

        KStream<String, String> simpleStream = builder.stream("stream-src-topic",
                Consumed.with(stringSerde, stringSerde));

        KStream<String, String> upperCasedStream = simpleStream.mapValues((ValueMapper<String, String>) String::toUpperCase);
        upperCasedStream.foreach((key, value) -> System.out.println(key + " --> " + value));

//        upperCasedStream.to("stream-out-topic",
//                Produced.with(stringSerde, stringSerde));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), props);
        kafkaStreams.start();
        Thread.sleep(35000);
        kafkaStreams.close();
    }
}
