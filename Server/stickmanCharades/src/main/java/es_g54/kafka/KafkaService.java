package es_g54.kafka;

import es_g54.utils.JSONTextDecoder;
import es_g54.utils.JSONTextEncoder;
import es_g54.websocket.Websocket;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.EncodeException;
import org.aerogear.kafka.SimpleKafkaProducer;
import org.aerogear.kafka.cdi.annotation.Consumer;
import org.aerogear.kafka.cdi.annotation.KafkaConfig;
import org.aerogear.kafka.cdi.annotation.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@KafkaConfig(bootstrapServers ="localhost:9092")
public class KafkaService{

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);
    
    @Inject
    private JSONTextEncoder jsonEncoder;
    
    @Inject
    private JSONTextDecoder jsonDecoder;
    
    @Inject
    private Websocket ws;

    @Producer
    SimpleKafkaProducer<Integer, JsonObject> producer;

    public void sendMessage() {
        try {
            LOGGER.info("prodeced value" );
            producer.send("othertopic", jsonDecoder.decode("{\"value\": \"My Message\"}"));
        } catch (DecodeException ex) {
            java.util.logging.Logger.getLogger(KafkaService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Consumer(topics = "topic", groupId = "esg54-group")
    public void onMessage(Integer key, JsonObject value) {
        try {
            LOGGER.info("We got this value: " + jsonEncoder.encode(value));
            this.ws.broadcastMessage(value);
        } catch (EncodeException ex) {
            java.util.logging.Logger.getLogger(KafkaService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}