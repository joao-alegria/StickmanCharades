/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ua.deti.es.g54.services;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaoalegria
 */
@Service
@EnableKafka
@Configuration
public class CommandsService {

    private static Logger logger = LoggerFactory.getLogger(CommandsService.class);
    
    private JSONParser jparser = new JSONParser();
    
    @Autowired
    private KafkaTemplate<String, String> kt;
    
    @Value("${KAFKA_BOOTSTRAP_SERVERS}")
    private String KAFKA_BOOTSTRAP_SERVERS;
    
    private ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_BOOTSTRAP_SERVERS
        );
        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "esp54_commandsConsumer"
        );
        properties.put(
                ConsumerConfig.CLIENT_ID_CONFIG,
                "esp54_commandsConsumer"
        );
        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
    
    @KafkaListener(topics="esp54_commandsServiceTopic")
    private void receiveCommand(String command){
        logger.info("Record received on listening topic");

        try {
            JSONObject json=(JSONObject)jparser.parse(command);
            processCommand(json);
        } catch (ParseException ex) {
            logger.error("Error while converting received record to json", ex);
        }
    }
    
    
    public JSONObject processCommand(JSONObject json){
        JSONObject message;
        switch((String)json.get("command")){
            case "notifyAdmin":
                message=new JSONObject();
                message.put("msg", "Notification forwarded to admin.");
                message.put("session", (String)json.get("session"));
                message.put("username", (String)json.get("username"));
                message.put("command", "notifyAdmin");
                kt.send("esp54_eventHandlerTopic", message.toJSONString());
                
                message.put("msg", "Notify admin.");
                kt.send("esp54_kafkaTranslatorTopic", message.toJSONString());
                
                message.put("type", "command");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());

                logger.info(String.format(
                    "Notify admin command sent for session %s by user %s",
                    json.get("session"),
                    json.get("username")
                ));
                break;
            case "stopSession":
                message=new JSONObject();
                message.put("msg", "Session ended.");
                message.put("session", (String)json.get("session"));
                message.put("username", (String)json.get("username"));
                message.put("command", "stopSession");
                kt.send("esp54_eventHandlerTopic", message.toJSONString());
                kt.send("esp54_kafkaTranslatorTopic", message.toJSONString());
                message.put("type", "execute");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());
                
                message.put("type", "command");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());

                logger.info(String.format(
                    "Stop session command sent for session %s by user %s",
                    json.get("session"),
                    json.get("username")
                ));
                break;
            case "startSession":
                message=new JSONObject();
                message.put("msg", "Session started.");
                message.put("session", (String)json.get("session"));
                message.put("username", (String)json.get("username"));
                message.put("command", "startSession");
                kt.send("esp54_eventHandlerTopic", message.toJSONString());
                kt.send("esp54_kafkaTranslatorTopic", message.toJSONString());
                message.put("type", "execute");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());
                
                message.put("type", "command");
                kt.send("esp54_databaseServiceTopic", message.toJSONString());

                logger.info(String.format(
                    "Start session command sent for session %s by user %s",
                    json.get("session"),
                    json.get("username")
                ));
                break;
        }
        
        JSONObject response = new JSONObject();
        response.put("msg", "Action performed with success.");
        return response;
        
    }
    
}
