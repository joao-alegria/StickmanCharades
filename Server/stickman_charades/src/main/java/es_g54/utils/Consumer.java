package es_g54.utils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 *
 * @author joaoalegria
 */
public class Consumer<K,V> implements Runnable{
    
    private Properties properties;
    private KafkaConsumer<K,V> consumer;
    
    private SimpMessagingTemplate smt;
    
    private volatile boolean done = false;

    @Value("${KAFKA_HOST}")
    private String KAFKA_HOST;

    @Value("${KAFKA_PORT}")
    private String KAFKA_PORT;

    public Consumer(String[] topics,SimpMessagingTemplate smt) {
        this.properties = new Properties();

        properties.put("bootstrap.servers", KAFKA_HOST + ":" + KAFKA_PORT);
        properties.put("group.id", "es_g54");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<K,V>(properties);
        this.consumer.subscribe(Arrays.asList(topics));
        this.smt=smt;
    }

    @Override
    public void run() {
        // while (true) {
        while(!done) {
            ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<K,V> record : records) {
                smt.convertAndSend("/session/banana", "isto é um teste");
            }
        }
        consumer.close();
    }

    public void shutdown() {
        done = true;
    }
    
}
