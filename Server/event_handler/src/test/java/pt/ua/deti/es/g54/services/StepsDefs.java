package pt.ua.deti.es.g54.services;


import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import pt.ua.deti.es.g54.Constants;

/**
 * Where all the steps of all features are defined here
 * Each step has referenced on javadoc on what Scenario(s) of which Feature(s) it is used
 */
@TestPropertySource (locations={"classpath:application-test.properties"})
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepsDefs {

    private static int usernameCount = 0;
    private static String currentUsername;
    private static long currentSessionId=0;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private KafkaTemplate<String, String> kt;

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

    private void changeCurrentUsername() {
        usernameCount++;
        currentUsername = "cucumber_tests" + usernameCount;
    }
    
    @Given("that I am logged in,")
    public void that_I_am_logged_in() throws Exception {
        changeCurrentUsername();
    }

    @Given("I am in a game session,")
    public void i_am_in_a_game_session() throws ParseException {
        currentSessionId++;
        String jsonBody = "{\"command\": \"startSession\", \"session\": \"esp54_"+currentSessionId+"\"}";
        kt.send(Constants.LISTENER_TOPIC, jsonBody);
    }

    @When("I raise my right hand above my head")
    public void i_raise_my_right_hand_above_my_head() {
        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
                + "\"LeftElbow\": [1751.346,713.7789,0.0], \"RightElbow\": [0.0,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [0.0,1193.861,0.0], "
                + "\"RightHand\": [0.0,1908.936,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
        kt.send("esp54_"+currentSessionId, jsonBody);
    }

    @Then("I should be notified that a message was send to the admin")
    public void i_should_be_notified_that_a_message_was_send_to_the_admin() throws ParseException {
        consumer.subscribe(Collections.singletonList(Constants.SESSION_COMMAND_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
        consumer.commitSync();
        assertEquals(1, records.count());
        for(ConsumerRecord<String,String> r : records){
            JSONObject json = (JSONObject) new JSONParser().parse(r.value());
            assertEquals(json.get("username"), currentUsername);
            assertEquals(json.get("command"), "notifyAdmin");
            assertEquals(json.get("session"), "esp54_"+currentSessionId);
        }
        consumer.unsubscribe();
    }

    @When("I raise my left hand above my head")
    public void i_raise_my_left_hand_above_my_head() {
        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
                + "\"LeftElbow\": [1751.346,713.7789,0.0], \"RightElbow\": [0.0,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [0.0,1908.936,0.0], "
                + "\"RightHand\": [0.0,1908.936,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
        kt.send("esp54_"+currentSessionId, jsonBody);
    }

    @When("I perform the initial position\\(spread arms)")
    public void i_perform_the_initial_position_spread_arms() throws ParseException {
        consumer.subscribe(Collections.singletonList(Constants.DATABASE_SERVICE_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
        consumer.commitSync();
        consumer.unsubscribe();
        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
                + "\"LeftElbow\": [1751.346,713.7789,0.0], \"RightElbow\": [0.0,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [0.0,1193.861,0.0], "
                + "\"RightHand\": [0.0,1175.847,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
        kt.send("esp54_"+currentSessionId, jsonBody);
    }

    @Then("I should be recognized by the platform")
    public void i_should_be_recognized_by_the_platform() throws ParseException, InterruptedException {
        consumer.subscribe(Collections.singletonList(Constants.DATABASE_SERVICE_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
        consumer.commitSync();
        assertEquals(1, records.count());
        for (ConsumerRecord<String,String> record : records){
            JSONObject json = (JSONObject) new JSONParser().parse(record.value());
            assertEquals(json.get("username"), currentUsername);
            assertEquals(json.get("event"), "initialPosition");
            assertEquals(json.get("session"), "esp54_"+currentSessionId);
        }
        consumer.unsubscribe();
    }

    @When("I perform the stopping position\\(cross arms over head)")
    public void i_perform_the_stopping_position_cross_arms_over_head() {
        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
                + "\"LeftElbow\": [2098.635,713.7789,0.0], \"RightElbow\": [2198.635,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [2198.635,1908.936,0.0], "
                + "\"RightHand\": [2098.635,1908.936,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
        kt.send("esp54_"+currentSessionId, jsonBody);
    }

    @Then("I should see the game session to be immediately stopped.") // next is this one
    public void i_should_see_the_game_session_to_be_immediately_stopped() throws ParseException {
        consumer.subscribe(Arrays.asList(Constants.SESSION_COMMAND_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
        consumer.commitSync();
        assertEquals(1, records.count());
        for (ConsumerRecord<String,String> record : records){
            JSONObject json = (JSONObject) new JSONParser().parse(record.value());
            System.out.println(json);
            assertEquals(json.get("command"), "stopSession");
            assertEquals(json.get("session"), "esp54_"+currentSessionId);
        }
        consumer.unsubscribe();
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
        /*
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(currentUsername, "123");

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", "test");
        jsonBody.put("duration", 600);

        HttpEntity entity = new HttpEntity(jsonBody, headers);

        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/session", HttpMethod.POST, entity, String.class);
        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
        currentSessionId=(long)json.get("id");
        assertEquals(result.getStatusCodeValue(),200);
         */
    }

    @Then("I should see a message informing me about the success\\/failure of the operation")
    public void i_should_see_a_message_informing_me_about_the_success_failure_of_the_operation() {
        //nothing to do; only validate UI
    }

    @Then("\\(if successful) I should be redirected to the session lobby.")
    public void if_successful_I_should_be_redirected_to_the_session_lobby() {
        //nothing to do; only validate UI
    }

    @Then("if am the last user recognized, the game session should start.")
    public void if_am_the_last_user_recognized_the_game_session_should_start() {
    }

}

//{"username": "joao", "positions": {"Head": [2098.635,1708.936,0.0], "Neck": [2227.439,1410.903,0.0], "LeftCollar": [2252.219,1217.666,0.0],"Torso": [2314.787,729.7457,0.0], "Waist": [2356.344,278.0999,0.0], "LeftShoulder": [1860.994,1193.861,0.0], "RightShoulder": [2813.465,1175.847,0.0], "LeftElbow": [2098.635,713.7789,0.0], "RightElbow": [2198.635,0.0,0.0], "LeftWrist": [0.0,0.0,0.0], "RightWrist": [0.0,0.0,0.0], "LeftHand": [2198.635,1908.936,0.0], "RightHand": [2098.635,1908.936,0.0], "LeftHip": [0.0,0.0,0.0], "RightHip": [2672.045,226.5393,0.0], "LeftKnee": [0.0,0.0,0.0], "RightKnee": [0.0,0.0,0.0], "LeftAnkle": [0.0,0.0,0.0], "RightAnkle": [0.0,0.0,0.0]}}