package pt.ua.deti.es.g54.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import pt.ua.deti.es.g54.Constants;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SessionMapper extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SessionMapper.class);

    private final Consumer<String, String> consumer;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final String session;

    private boolean closed;

    public SessionMapper(String session, SimpMessagingTemplate simpMessagingTemplate) {
        logger.info("Initializing SessionMapper for session " + session);

        Properties properties = new Properties();
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

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(session));

        this.session = session;
        this.simpMessagingTemplate = simpMessagingTemplate;
        closed = false;
    }

    public void sessionClosed() {
        logger.info(String.format(
            "Tell session mapper for session %s to exit",
            session
        ));
        closed = true;
    }

    @Override
    public void run() {
        logger.info(String.format(
                "SessionMapper for session %s started",
                session
        ));

        while (!closed) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record : records) {
                logger.error(String.format(
                    "Resending parsing received record for session %s websoctet",
                    session
                ));

                simpMessagingTemplate.convertAndSend(
                        "/game/session/" + session,
                        record.value()
                );
            }
        }

        logger.info("Session mapper for session" + session + " is exiting");

        consumer.close();
    }

}
