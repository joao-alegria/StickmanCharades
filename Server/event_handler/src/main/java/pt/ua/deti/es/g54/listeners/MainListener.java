package pt.ua.deti.es.g54.listeners;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;

@EnableKafka
@Configuration
public class MainListener {

    private static final Logger logger = LoggerFactory.getLogger(MainListener.class);

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
        logger.info("Record received on listening topic");

        JSONObject message;
        try {
            message = (JSONObject) parser.parse(message_string);
        } catch (ParseException e) {
            logger.error("Error while converting received record to json", e);
            return;
        }

        String session = (String) message.get("session");
        switch ((String) message.get("command")) {
            case "stopSession":
                logger.info("Stop session message received for session " + session);
                synchronized (eventHandlers) {
                    EventHandler eventHandler = eventHandlers.get(session);
                    if (eventHandler != null) {
                        eventHandler.sessionClosed();
                        eventHandlers.remove(session);
                    }
                    else {
                        logger.error(String.format(
                            "Unable to stop session %s. There is no event handler associated with it",
                            session
                        ));
                    }
                }
                break;
            case "startSession":
                logger.info("Start session message received");

                if (!eventHandlers.containsKey(session)) {
                    EventHandler eventHandler = new EventHandler(session, kafkaTemplate);
                    synchronized (eventHandlers) {
                        eventHandlers.put(
                                session,
                                eventHandler
                        );
                    }
                    eventHandler.start();
                }
                else {
                    logger.error(String.format(
                        "There is already a event handler for session %s",
                        session
                    ));
                }
                break;
            default:
                logger.error(String.format(
                    "Unknown command received (%s)",
                    message.get("command")
                ));
        }
    }
}
