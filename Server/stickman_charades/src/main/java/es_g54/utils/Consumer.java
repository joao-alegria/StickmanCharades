package es_g54.utils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 *
 * @author joaoalegria
 */
public class Consumer implements Runnable{
    
    private Properties properties;
    private KafkaConsumer<Integer, String> consumer;
    
    private SimpMessagingTemplate smt;
    
    private String topic;
    
    private volatile boolean done = false;
    
    private static final JSONParser parser = new JSONParser();
    
    private SkeletonProcessor sp;

    public Consumer(String KAFKA_HOST, String KAFKA_PORT,String topic,SimpMessagingTemplate smt, SkeletonProcessor sp) {
        this.properties = new Properties();

        properties.put("bootstrap.servers", KAFKA_HOST + ":" + KAFKA_PORT);
        properties.put("group.id", "es_g54_group_"+topic);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<Integer,String>(properties);
        this.consumer.subscribe(Arrays.asList(topic));
        this.smt=smt;
        this.topic=topic;
        this.sp=sp;
    }

    @Override
    public void run() {
        // while (true) {
        while(!done) {
            ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<Integer,String> record : records) {
                try {
                    JSONObject json = (JSONObject)parser.parse(record.value());
                    System.out.println(json);
                    sp.process(json);
                    smt.convertAndSend("/game/session/"+this.topic, record.value());
                } catch (ParseException ex) {
                    Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        consumer.close();
    }

    public void shutdown() {
        done = true;
    }
    
}
