package pt.ua.deti.es.g54.listeners;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Component;
import pt.ua.deti.es.g54.Constants;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

public class EventHandler extends Thread {

    private final Consumer<String, String> consumer;

    private final String session;

    private boolean closed;

    private static final JSONParser parser = new JSONParser();

    private static final Random random = new Random();

    private int currentPlayingPlayerIdx;

    private String currentWord;

    private final Map<String, Integer> playersScore;

    private final Set<String> activePlayers;

    private final List<String> players;

    private final List<String> wordPool;

    private KafkaTemplate<String, String> kafkaTemplate;

    public EventHandler(String sessionId, KafkaTemplate<String, String> kafkaTemplate, List<String> players, List<String> wordPool) {
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
        consumer.subscribe(Collections.singletonList(sessionId));

        this.session = sessionId;
        this.kafkaTemplate = kafkaTemplate;
        this.players = players;
        this.wordPool = wordPool;

        closed = false;
        currentPlayingPlayerIdx = 0;

        playersScore = new HashMap<>();
        activePlayers = new HashSet<>();
    }

    public void sessionClosed() {
        closed = true;
    }

    @Override
    public void run() {
        while (!closed) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record : records) {
                JSONObject json;
                try {
                    json = (JSONObject) parser.parse(record.value());
                } catch (ParseException e) {
                    e.printStackTrace();
                    // TODO log. parse json error
                    continue;
                }

                try {
                    handleMessage(json);
                }
                catch (MissingKeyException e) {
                    // TODO log. missing key
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    // TODO log. no data on positions fields
                }
            }
        }

        consumer.close();
    }

    private void handleMessage(JSONObject message) throws MissingKeyException {
        JSONObject event = new JSONObject();

        String username = (String) getValueFromKey(message, "username");

        if (message.containsKey("msg")){
            String msgTag = (String) getValueFromKey(message, "msg");
            if (msgTag.equals("wordGuess")) {
                event.put("event", "wordGuess");
                event.put("username", username);
                event.put("guess", getValueFromKey(message, "word"));

                event.put("correct", msgTag.equals(currentWord));

                kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());
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
            event.put("event", "stopSession");
            event.put("session", session);

            kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());
        }
        else if ((((double)rightHand.get(1)) > ((double)head.get(1)))
                 ||
                 (((double)leftHand.get(1)) > ((double)head.get(1)))) {  //raise hand above head
            event.put("event", "notifyAdmin");
            event.put("session", session);
            event.put("username", username);

            kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());
        }

        if (activePlayers.size() < players.size() && !activePlayers.contains(username)) {
            double leftDist = Math.abs(((double)leftHand.get(1)) - ((double)leftShoulder.get(1)));
            double rightDist = Math.abs(((double)rightHand.get(1)) - ((double)rightShoulder.get(1)));
            if (leftDist < 50 && rightDist < 50) {
                activePlayers.add(username);
                event.put("event", "playerReady");
                event.put("session", session);
                event.put("username", username);
                kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());
            }

            if (activePlayers.size() == players.size()) {
                event.put("event", "allPlayersReady");
                event.put("username", players.get(currentPlayingPlayerIdx));
                currentWord = wordPool.get(random.nextInt(wordPool.size()));
                event.put("wordToGuess", currentWord);

                kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());

                currentPlayingPlayerIdx++;
            }
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
