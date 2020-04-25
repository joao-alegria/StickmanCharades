package pt.ua.deti.es.g54.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import pt.ua.deti.es.g54.entities.DBSession;
import pt.ua.deti.es.g54.entities.DBUser;
import pt.ua.deti.es.g54.repository.SessionRepository;

/**
 *
 * @author joaoalegria
 */
public class SessionConsumer implements Runnable{
    
    private Properties properties;
    private KafkaConsumer<Integer, String> consumer;
    
    private SimpMessagingTemplate smt;
    
    private DBSession session;
    
    private String topic;
    
    private volatile boolean done = false;
    
    private static final JSONParser parser = new JSONParser();
    
    private Thread sessionThread;
    
    private List<String> activePlayers= new ArrayList();
    
    private SessionRepository sr;
    

    public SessionConsumer(String KAFKA_HOST, String KAFKA_PORT, DBSession session, String topic,SimpMessagingTemplate smt, SessionRepository sr) {
        this.properties = new Properties();

        properties.put("bootstrap.servers", KAFKA_HOST + ":" + KAFKA_PORT);
        properties.put("group.id", "es_g54_group_"+topic);
        properties.put("auto.offset.reset", "latest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<Integer,String>(properties);
        this.consumer.subscribe(Arrays.asList(topic));
        this.smt=smt;
        this.topic=topic;
        this.session=session;
        this.sr=sr;
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
                    
                    JSONArray head = ((JSONArray)((JSONObject)json.get("positions")).get("Head"));
                    JSONArray leftHand = ((JSONArray)((JSONObject)json.get("positions")).get("LeftHand"));
                    JSONArray rightHand = ((JSONArray)((JSONObject)json.get("positions")).get("RightHand"));
                    JSONArray rightElbow = ((JSONArray)((JSONObject)json.get("positions")).get("RightElbow"));
                    JSONArray leftElbow = ((JSONArray)((JSONObject)json.get("positions")).get("LeftElbow"));
                    JSONArray leftShoulder = ((JSONArray)((JSONObject)json.get("positions")).get("LeftShoulder"));
                    JSONArray rightShoulder = ((JSONArray)((JSONObject)json.get("positions")).get("RightShoulder"));
                    if((((double)rightHand.get(1))>((double)head.get(1))) && (((double)leftHand.get(1))>((double)head.get(1))) && 
                       (((((double)rightElbow.get(0))>((double)leftElbow.get(0))) && (((double)rightHand.get(0))<((double)leftHand.get(0)))) || 
                       ((((double)rightElbow.get(0))<((double)leftElbow.get(0))) && (((double)rightHand.get(0))>((double)leftHand.get(0)))))){  //cross arms above head
                        //stop session
                        sessionThread.interrupt();
                        done=true;
                    }else if((((double)rightHand.get(1))>((double)head.get(1))) || (((double)leftHand.get(1))>((double)head.get(1)))){  //raise hand above head
                        //notify admin
                        JSONObject adminNotification = new JSONObject();
                        adminNotification.put("session", topic);
                        adminNotification.put("user", json.get("username"));
                        smt.convertAndSend("/game/admin", adminNotification.toJSONString());
                    }
                    
                    
                    if(activePlayers.size()==session.getPlayers().size()){
                        smt.convertAndSend("/game/session/"+this.topic, record.value());
                    }else{
                        if(!activePlayers.contains(json.get("username"))){
                            double leftDist=Math.abs(((double)leftHand.get(1))-((double)leftShoulder.get(1)));
                            double rightDist=Math.abs(((double)rightHand.get(1))-((double)rightShoulder.get(1)));
                            if(leftDist<10 && rightDist<10){
                                activePlayers.add((String)json.get("username"));
                            }
                            
                            if(activePlayers.size()==session.getPlayers().size()){
                                Thread sessionThread = new Thread(new Session(session.getDurationSeconds()));
                                sessionThread.start();
                            }
                        }
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(SessionConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        consumer.close();
    }

    public void shutdown() {
        done = true;
    }
    
    
    private class Session implements Runnable{
        
        private int durationSeconds;
        private int timeToWait;

        public Session(int durationSeconds) {
            this.durationSeconds = durationSeconds;
            this.timeToWait = durationSeconds;
        }

        @Override
        public void run() {
            try{
                while(this.timeToWait>0){
                    Thread.sleep(this.timeToWait);
                    this.timeToWait=session.getDurationSeconds()-this.durationSeconds;
                    this.durationSeconds=session.getDurationSeconds();
                }
            }catch(InterruptedException ex){
                this.timeToWait=session.getDurationSeconds()-this.durationSeconds;
                this.durationSeconds=session.getDurationSeconds();

                if(!session.getIsAvailable()){
                    this.timeToWait=0;
                }
            }
            
            session.setIsAvailable(false);
            sr.save(session); 
        }
    }
    
}
