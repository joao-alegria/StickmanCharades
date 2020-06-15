package pt.ua.deti.es.g54.services;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import pt.ua.deti.es.g54.Constants;

/**
 * Where all the steps of all features are defined here
 * Each step has referenced on javadoc on what Scenario(s) of which Feature(s) it is used
 */
@TestPropertySource (locations={"classpath:application-test.properties"})
@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StepsDefs {

    private static int usernameCount = 0;
    private static long currentSessionId;

    @LocalServerPort
    int randomServerPort;
    
    @Autowired
    private KafkaTemplate<String, String> kt;
    
    private KafkaConsumer consumer;
    BlockingQueue<String> blockingQueue;

    @Value("${KAFKA_BOOTSTRAP_SERVERS}")
    private String KAFKA_BOOTSTRAP_SERVERS;

    @Before
    public void stetUp() throws InterruptedException, ExecutionException{
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
        String jsonBody = "{\"command\": \"notifyAdmin\", \"session\": \"esp54_"+currentSessionId+"\", \"username\":\"testUser1\",\"msg\":\"Notified admin\"}";
        kt.send(Constants.LISTENER_TOPIC, jsonBody);
    }

    @Then("I should be notified that a message was send to the admin")
    public void i_should_be_notified_that_a_message_was_send_to_the_admin() throws ParseException, InterruptedException {
        consumer.subscribe(Collections.singletonList("esp54_" + currentSessionId));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(15));
        consumer.commitSync();
        assertEquals(1, records.count());
        for(ConsumerRecord<String,String> r : records){
            JSONObject json = (JSONObject) new JSONParser().parse(r.value());
            assertEquals(json.get("user"), "testUser1");
            assertEquals(json.get("session"), "esp54_"+currentSessionId);
            assertEquals(json.get("msg"), "Notified admin");
        }
        consumer.unsubscribe();
    }

    @When("I raise my left hand above my head")
    public void i_raise_my_left_hand_above_my_head() {
        String jsonBody = "{\"command\": \"notifyAdmin\", \"session\": \"esp54_"+currentSessionId+"\", \"username\":\"testUser1\",\"msg\":\"Notified admin\"}";
        kt.send(Constants.LISTENER_TOPIC, jsonBody);
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


    @When("I perform the initial position\\(spread arms)")
    public void i_perform_the_initial_position_spread_arms() throws ParseException {
    }

    @Then("I should be recognized by the platform")
    public void i_should_be_recognized_by_the_platform() throws ParseException, InterruptedException {
    }

    @Then("if am the last user recognized, the game session should start.")
    public void if_am_the_last_user_recognized_the_game_session_should_start() {

    }

    @When("I perform the stopping position\\(cross arms over head)")
    public void i_perform_the_stopping_position_cross_arms_over_head() {
    }

    @Then("I should see the game session to be immediately stopped.")
    public void i_should_see_the_game_session_to_be_immediately_stopped() throws ParseException {
    }
    
}
