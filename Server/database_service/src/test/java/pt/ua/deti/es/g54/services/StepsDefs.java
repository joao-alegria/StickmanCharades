package pt.ua.deti.es.g54.services;


import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import pt.ua.deti.es.g54.Constants;
import pt.ua.deti.es.g54.api.entities.UserData;

/**
 * Where all the steps of all features are defined here
 * Each step has referenced on javadoc on what Scenario(s) of which Feature(s) it is used
 */
@TestPropertySource (locations={"classpath:application-test.properties"})
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepsDefs {

    private static int usernameCount = 0;
    private static String currentUsername;
    private static long currentSessionId;

    private KafkaConsumer consumer;

    @Value("${KAFKA_BOOTSTRAP_SERVERS}")
    private String KAFKA_BOOTSTRAP_SERVERS;

    @Before
    public void stetUp(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS);
        properties.put("group.id", "es_g54_group_test");
        properties.put("auto.offset.reset", "latest");
        properties.put("auto.commit.enable", "false");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<String,String>(properties);
    }

    @LocalServerPort
    int randomServerPort;
    
    @Autowired
    private TestRestTemplate restTemplate;  
    
    @Autowired
    private UserService us;
    
    @Autowired
    private KafkaTemplate kt;
    
    private String server="http://localhost:";

    private void changeCurrentUsername() {
        usernameCount++;
        currentUsername = "cucumber_tests" + usernameCount;
    }
    
    @Given("that I am logged in,")
    public void that_I_am_logged_in() throws Exception {
        changeCurrentUsername();
        UserData ud = new UserData();
        ud.setUsername(currentUsername);
        ud.setEmail(currentUsername+"@mail.com");
        ud.setPassword("123".toCharArray());
        us.registerUser(ud);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(currentUsername, "123");
        
        HttpEntity entity = new HttpEntity(headers);
        
        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/login", HttpMethod.GET, entity, String.class);
        assertEquals(200, result.getStatusCodeValue());
    }

    @Given("I am in a game session,")
    public void i_am_in_a_game_session() throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(currentUsername, "123");
        
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", "test_"+currentUsername);
        jsonBody.put("duration", 600);
        List<String> words=new ArrayList();
        words.add("banana");
        words.add("pera");
        jsonBody.put("words", words);
        
        HttpEntity entity = new HttpEntity(jsonBody, headers);
        
        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/session", HttpMethod.POST, entity, String.class);
        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
        currentSessionId=(long)json.get("id");
        assertEquals(200, result.getStatusCodeValue());
    }

    @When("I raise my right hand above my head")
    public void i_raise_my_right_hand_above_my_head() {
        String jsonBody = "{\"type\": \"event\", \"event\": \"raisedRightHand\", \"username\":\"testUser1\", \"session\":\"esp54_1\", \"time\":12345}";
        kt.send(Constants.LISTENING_TOPIC, jsonBody);
    }

    @When("I raise my left hand above my head")
    public void i_raise_my_left_hand_above_my_head() {
        String jsonBody = "{\"type\": \"event\", \"event\": \"raisedLeftHand\", \"username\":\"testUser1\", \"session\":\"esp54_1\", \"time\":12345}";
        kt.send(Constants.LISTENING_TOPIC, jsonBody);
    }

    @When("I perform the initial position\\(spread arms)")
    public void i_perform_the_initial_position_spread_arms() throws ParseException {
        String jsonBody = "{\"type\": \"event\", \"event\": \"initialPosition\", \"username\":\""+currentUsername+"\", \"session\":\""+"esp54_"+currentSessionId+"\", \"time\":12345}";
        kt.send(Constants.LISTENING_TOPIC, jsonBody);
    }

    @Then("I should be recognized by the platform")
    public void i_should_be_recognized_by_the_platform() throws ParseException, InterruptedException {
        consumer.subscribe(Collections.singletonList("esp54_" + currentSessionId));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
        consumer.commitSync();
        assertEquals(1, records.count());
        for(ConsumerRecord<String,String> r : records){
            JSONObject json = (JSONObject) new JSONParser().parse(r.value());        
            assertEquals(json.get("type"), "gameInfo");
            assertEquals(json.get("info"), "User Recognized.");
        }
        consumer.unsubscribe();
    }

    @Then("if am the last user recognized, the game session should start.")
    public void if_am_the_last_user_recognized_the_game_session_should_start() throws ParseException {
        consumer.subscribe(Collections.singletonList(Constants.COMMANDS_SERVICE_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
        consumer.commitSync();
        assertEquals(1, records.count());
        for(ConsumerRecord<String,String> r : records){
            JSONObject json = (JSONObject) new JSONParser().parse(r.value());
            assertEquals(json.get("command"), "startSession");
            assertEquals(json.get("session"), "esp54_"+currentSessionId);
            assertEquals(json.get("username"), currentUsername);
        }
        consumer.unsubscribe();
    }

    @When("I perform the stopping position\\(cross arms over head)")
    public void i_perform_the_stopping_position_cross_arms_over_head() {
        String jsonBody = "{\"type\": \"execute\", \"command\": \"stopSession\", \"session\":\""+"esp54_"+currentSessionId+"\"}";
        kt.send(Constants.LISTENING_TOPIC, jsonBody);
    }

    @When("I choose the option to create a game session")
    public void i_choose_the_option_to_create_a_game_session() {
        //nothing to do; only validate UI
    }

    @Then("I should see a form to be filled.")
    public void i_should_see_a_form_to_be_filled() {
        //nothing to do; only validate UI
    }

    @When("I execute the previous steps")
    public void i_execute_the_previous_steps() {
        //nothing to do; only validate UI
    }

    @When("I fill in and submit the form,")
    public void i_fill_in_and_submit_the_form() throws ParseException {
    }

    @Then("I should see a message informing me about the success\\/failure of the operation")
    public void i_should_see_a_message_informing_me_about_the_success_failure_of_the_operation() {
        //nothing to do; only validate UI
    }

    @Then("\\(if successful) I should be redirected to the session lobby.")
    public void if_successful_I_should_be_redirected_to_the_session_lobby() {
        //nothing to do; only validate UI
    }

    @Then("I should be notified that a message was send to the admin")
    public void i_should_be_notified_that_a_message_was_send_to_the_admin() throws ParseException, InterruptedException {
    }

    @Then("I should see the game session to be immediately stopped.")
    public void i_should_see_the_game_session_to_be_immediately_stopped() throws ParseException {
    }

}
