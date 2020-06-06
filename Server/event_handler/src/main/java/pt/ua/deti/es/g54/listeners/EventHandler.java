package pt.ua.deti.es.g54.listeners;

import io.micrometer.core.instrument.Counter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import pt.ua.deti.es.g54.Constants;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class EventHandler extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private static final JSONParser parser = new JSONParser();

    private final Consumer<String, String> consumer;

    private final String session;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Counter notifyAdmin;

    private final Counter stopSession;

    private final Counter wordGuess;

    private final Counter initialPosition;

    private boolean closed;

    private long lastNotifyAdminTimestamp;

    public EventHandler(String sessionId, KafkaTemplate<String, String> kafkaTemplate, String KAFKA_BOOTSTRAP_SERVERS, Counter notifyAdmin, Counter stopSession, Counter wordGuess, Counter initialPosition) {
        logger.info(String.format(
            "Initializing EventHandler for session %s",
            sessionId
        ));

        Properties properties = new Properties();
        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_BOOTSTRAP_SERVERS
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
        consumer.subscribe(Collections.singletonList(sessionId));

        this.session = sessionId;
        this.kafkaTemplate = kafkaTemplate;
        this.notifyAdmin = notifyAdmin;
        this.stopSession = stopSession;
        this.wordGuess = wordGuess;
        this.initialPosition = initialPosition;

        closed = false;

        lastNotifyAdminTimestamp = -1;
    }

    public void sessionClosed() {
        logger.info("Tell event handler for session" + session + " to exit");
        closed = true;
    }

    @Override
    public void run() {
        logger.info(String.format(
                "EventHandler for session %s started",
                session
        ));

        while (!closed) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record : records) {
                logger.info("Parsing a record");
                JSONObject json;
                try {
                    json = (JSONObject) parser.parse(record.value());
                } catch (ParseException e) {
                    logger.error("Error while converting received record to json", e);
                    continue;
                }

                try {
                    handleMessage(json);
                }
                catch (MissingKeyException e) {
                    logger.error(
                        String.format(
                            "Received message (%s) on session %s is missing key %s",
                            record.value(),
                            session,
                            e.getMissingKey()
                        )
                    );
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    logger.error("Missing data on some positions field", e);
                }
            }
        }

        logger.info("Event handler for session" + session + " is exiting");

        consumer.close();
    }

    private void handleMessage(JSONObject message) throws MissingKeyException {
        JSONObject event = new JSONObject();

        String username = (String) getValueFromKey(message, "username");

        if (message.containsKey("msg")){
            String msgTag = (String) getValueFromKey(message, "msg");
            if (msgTag.equals("wordGuess")) {
                synchronized (wordGuess) {
                    wordGuess.increment();
                }

                logger.info(String.format(
                    "WordGuess message received (%s). Sending to Database service.",
                    message.toJSONString()
                ));

                event.put("type", "wordGuess");
                event.put("session", session);
                event.put("username", username);
                event.put("guess", getValueFromKey(message, "word"));

                kafkaTemplate.send(Constants.DATABASE_SERVICE_TOPIC, event.toJSONString());
            }

            return;
        }

        JSONObject positions = (JSONObject) getValueFromKey(message, "positions");
        JSONArray head = (JSONArray) getValueFromKey(positions, "Head");
        JSONArray leftHand = (JSONArray) getValueFromKey(positions, "LeftHand");
        JSONArray rightHand = (JSONArray) getValueFromKey(positions, "RightHand");
        JSONArray rightElbow = (JSONArray) getValueFromKey(positions, "RightElbow");
        JSONArray leftElbow = (JSONArray) getValueFromKey(positions, "LeftElbow");
        JSONArray leftShoulder = (JSONArray) getValueFromKey(positions, "LeftShoulder");
        JSONArray rightShoulder = (JSONArray) getValueFromKey(positions, "RightShoulder");    
        if ((((double)rightHand.get(1))>((double)head.get(1))) &&
            (((double)leftHand.get(1))>((double)head.get(1))) &&
                (((((double)rightElbow.get(0))>((double)leftElbow.get(0))) &&
                (((double)rightHand.get(0))<((double)leftHand.get(0))))
                ||
                ((((double)rightElbow.get(0))<((double)leftElbow.get(0))) &&
                (((double)rightHand.get(0))>((double)leftHand.get(0)))))) { //cross arms above head
            synchronized (stopSession) {
                stopSession.increment();
            }

            logger.info(String.format(
                "Stop session event from user %s on session %s",
                username,
                session
            ));

            event.put("command", "stopSession");
            event.put("session", session);
            event.put("username", username);

            kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());

            event.remove("command");
            event.put("type", "event");
            event.put("event", "armsCrossedAboveHead");
            event.put("time", System.currentTimeMillis());

            kafkaTemplate.send(Constants.DATABASE_SERVICE_TOPIC, event.toJSONString());
        }
        else if (((((double)rightHand.get(1)) > ((double)head.get(1)))
                 ||
                 (((double)leftHand.get(1)) > ((double)head.get(1)))) &&  // raise hand above head
                 System.currentTimeMillis() - lastNotifyAdminTimestamp >= 5000) {  // alert with a interval of 5 seconds
            synchronized (notifyAdmin) {
                notifyAdmin.increment();
            }

            logger.info(String.format(
                "Notify admin event from user %s on session %s",
                username,
                session
            ));

            event.put("command", "notifyAdmin");
            event.put("session", session);
            event.put("username", username);

            kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());

            event.remove("command");
            event.put("type", "event");
            event.put("event", "handsAboveHead");
            event.put("time", System.currentTimeMillis());

            kafkaTemplate.send(Constants.DATABASE_SERVICE_TOPIC, event.toJSONString());

            lastNotifyAdminTimestamp = System.currentTimeMillis();
        }

        double leftDist = Math.abs(((double)leftHand.get(1)) - ((double)leftShoulder.get(1)));
        double rightDist = Math.abs(((double)rightHand.get(1)) - ((double)rightShoulder.get(1)));
        if (leftDist < 50 && rightDist < 50) {
            synchronized (initialPosition) {
                initialPosition.increment();
            }

            logger.info(String.format(
                "%s ready",
                username
            ));

            event.put("type", "event");
            event.put("event", "initialPosition");
            event.put("session", session);
            event.put("username", username);
            event.put("time", System.currentTimeMillis());
            kafkaTemplate.send(Constants.DATABASE_SERVICE_TOPIC, event.toJSONString());   //VERIFY THIS!!!!!!!!!!!!!!!!!!!!!!!
        }
    }

    public Object getValueFromKey(JSONObject json, String key) throws MissingKeyException {
        if (json.containsKey(key)) {
            return json.get(key);
        }
        else {
            throw new MissingKeyException(key);
        }
    }
}
