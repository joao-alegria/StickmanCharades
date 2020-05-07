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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pt.ua.deti.es.g54.Constants;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EventHandler extends Thread {

    private final Consumer<String, String> consumer;

    private final String session;

    private boolean closed;

    private static final JSONParser parser = new JSONParser();

    private int currentPlayingPlayerIdx;

    private String currentWord;

    private final Map<String, Integer> playersScore;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public EventHandler(String sessionId) {
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
        closed = false;
        currentPlayingPlayerIdx = 0;
        playersScore = new HashMap<>();

    }

    public void sessionClosed() {
        closed = true;
    }

    @Override
    public void run() {
        while (!closed) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record : records) {
                try {
                    JSONObject json = (JSONObject) parser.parse(record.value());
                } catch (ParseException e) {
                    e.printStackTrace();
                    // TODO log. parse json error
                }
            }
        }

        consumer.close();
    }

    private void handleMessage(JSONObject message) {
        if (message.containsKey("msg")){
            String msgTag = (String) message.get("msg");
            if (msgTag.equals("Word guess") && msgTag.equals(currentWord)) {

                JSONObject event = new JSONObject();
                event.put("event", "wordGuess");
                event.put("username", message.get("username"));
                event.put("word", message.get("word"));

                kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());
            }

            return;
        }

        JSONArray head = ((JSONArray)((JSONObject)message.get("positions")).get("Head"));
        JSONArray leftHand = ((JSONArray)((JSONObject)message.get("positions")).get("LeftHand"));
        JSONArray rightHand = ((JSONArray)((JSONObject)message.get("positions")).get("RightHand"));
        JSONArray rightElbow = ((JSONArray)((JSONObject)message.get("positions")).get("RightElbow"));
        JSONArray leftElbow = ((JSONArray)((JSONObject)message.get("positions")).get("LeftElbow"));
        JSONArray leftShoulder = ((JSONArray)((JSONObject)message.get("positions")).get("LeftShoulder"));
        JSONArray rightShoulder = ((JSONArray)((JSONObject)message.get("positions")).get("RightShoulder"));
        if ((((double)rightHand.get(1))>((double)head.get(1))) &&
            (((double)leftHand.get(1))>((double)head.get(1))) &&
                (((((double)rightElbow.get(0))>((double)leftElbow.get(0))) &&
                (((double)rightHand.get(0))<((double)leftHand.get(0))))
                ||
                ((((double)rightElbow.get(0))<((double)leftElbow.get(0))) &&
                (((double)rightHand.get(0))>((double)leftHand.get(0)))))) { //cross arms above head


            JSONObject event = new JSONObject();
            event.put("event", "stopSession");
            event.put("session", session);

            kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());
        }
        else if ((((double)rightHand.get(1))>((double)head.get(1))) || (((double)leftHand.get(1))>((double)head.get(1)))) {  //raise hand above head
            //notify admin

            JSONObject event = new JSONObject();
            event.put("event", "notifyAdmin");
            event.put("session", session);
            event.put("username", message.get("username"));

            kafkaTemplate.send(Constants.SESSION_COMMAND_TOPIC, event.toJSONString());
        }

        if(activePlayers.size()==session.getPlayers().size()){
            smt.convertAndSend("/game/session/"+this.topic, record.value());
        }else{
            if(!activePlayers.contains(message.get("username"))){
                double leftDist=Math.abs(((double)leftHand.get(1))-((double)leftShoulder.get(1)));
                double rightDist=Math.abs(((double)rightHand.get(1))-((double)rightShoulder.get(1)));
                if(leftDist<50 && rightDist<50){
                    activePlayers.add((String)message.get("username"));
                    JSONObject notification = new JSONObject();
                    notification.put("username", (String)message.get("username"));
                    notification.put("msg", "User recognized.");
                    kt.send(topic, notification.toJSONString());
                }

                if(activePlayers.size()==session.getPlayers().size()){
                    session.setIsActive(true);
                    sr.save(session);
                    sessionThread = new Thread(new Session(session.getDurationSeconds()));
                    sessionThread.start();
                    JSONObject message = new JSONObject();
                    message.put("username", activePlayers.get(currentPlayingPlayerIdx));
                    currentWord=session.getRandomWord();
                    message.put("word", currentWord);
                    message.put("msg", "Word to mimic.");
                    kt.send(topic, message.toJSONString());
                    currentPlayingPlayerIdx++;
                }
            }
        }
    }

}
