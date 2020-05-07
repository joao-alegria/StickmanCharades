package pt.ua.deti.es.g54.listeners;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import pt.ua.deti.es.g54.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableKafka
@Configuration
public class MainListener {

    private static final JSONParser parser = new JSONParser();

    private static final Map<String, EventHandler> eventHandlers = new HashMap<>();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                Constants.BOOTSTRAP_SERVERS
        );
        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                Constants.GROUP_ID
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

    @KafkaListener(topics = Constants.LISTENER_TOPIC)
    public void listen(String message_string) {
        JSONObject message;
        try {
            message = (JSONObject) parser.parse(message_string);
        } catch (ParseException e) {
            e.printStackTrace();
            // TODO log. invalid message
            return;
        }

        String session = (String) message.get("session");
        switch ((String) message.get("command")) {
            case "stopSession":
                synchronized (eventHandlers) {
                    EventHandler eventHandler = eventHandlers.get(session);
                    if (eventHandler != null) {
                        eventHandler.sessionClosed();
                        eventHandlers.remove(session);
                    }
                    else {
                        // TODO log. no session mapper for the received session
                    }
                }
                break;
            case "startSession":
                if (!eventHandlers.containsKey(session)) {
                    List<String> wordPool = (JSONArray) message.get("wordPool");
                    List<String> players = (JSONArray) message.get("players");
                    EventHandler eventHandler = new EventHandler(session, kafkaTemplate, players, wordPool);
                    synchronized (eventHandlers) {
                        eventHandlers.put(
                                session,
                                eventHandler
                        );
                    }
                    eventHandler.start();
                }
                break;
            default:
                // TODO log. unknown message
        }
    }
}
