//package pt.ua.deti.es.g54.services;
//
//
//import cucumber.api.java.Before;
//import cucumber.api.java.en.Given;
//import cucumber.api.java.en.Then;
//import cucumber.api.java.en.When;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import java.time.Duration;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Properties;
//import junit.framework.Assert;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import static org.assertj.core.api.Assertions.assertThat;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.junit.After;
//import static org.junit.Assert.assertEquals;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.test.EmbeddedKafkaBroker;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.context.TestPropertySource;
//import pt.ua.deti.es.g54.api.entities.UserData;
//import pt.ua.deti.es.g54.entities.DBSession;
//import pt.ua.deti.es.g54.entities.DBUser;
//import pt.ua.deti.es.g54.repository.SessionRepository;
//import pt.ua.deti.es.g54.repository.UserRepository;
//
///**
// * Where all the steps of all features are defined here
// * Each step has referenced on javadoc on what Scenario(s) of which Feature(s) it is used
// */
//@TestPropertySource (locations={"classpath:application-test.properties"})
//@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@EmbeddedKafka
//public class StepsDefs {
//
//    private long MAX_WAIT_TIME = 500;
//
//    private static int usernameCount = 0;
//    private static String currentUsername;
//    private static String currentFriendname;
//    private static long currentSessionId;
//
//    @Autowired
//    private EmbeddedKafkaBroker embeddedKafkaBroker;
//
//    @Before
//    public void before() {
//        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", embeddedKafkaBroker.getBrokersAsString());
//    }
//
//    @LocalServerPort
//    int randomServerPort;
//    
//    @Autowired
//    private TestRestTemplate restTemplate;  
//    
//    @Autowired
//    private UserService us;
//    
//    @Autowired
//    private KafkaTemplate kt;
//    
//    @Autowired
//    private SessionRepository sr;
//    
//    @Autowired
//    private UserRepository ur;
//    
//    private String server="http://localhost:";
//
//    //private static WebDriver driver;
//
//    //static {
//    //    ChromeOptions options = new ChromeOptions();
//    //    options.addArguments("--whitelisted-ips");
//    //    options.addArguments("--headless");
//    //    options.addArguments("--no-sandbox");
//    //    options.addArguments("--disabled-extensions");
//
//    //    WebDriverManager.chromedriver().setup();
//    //    driver = new ChromeDriver(options);
//    //}
//    
//    private void changeCurrentUsername() {
//        usernameCount++;
//        currentUsername = "cucumber_tests" + usernameCount;
//    }
//    
//    @Given("that I am logged in,")
//    public void that_I_am_logged_in() throws Exception {
//        changeCurrentUsername();
//        UserData ud = new UserData();
//        ud.setUsername(currentUsername);
//        ud.setEmail(currentUsername+"@mail.com");
//        ud.setPassword("123".toCharArray());
//        us.registerUser(ud);
//        
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        HttpEntity entity = new HttpEntity(headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/login", HttpMethod.GET, entity, String.class);
//        assertEquals(result.getStatusCodeValue(),200);
//    }
//
//    @When("I choose the option to create a game session")
//    public void i_choose_the_option_to_create_a_game_session() {
//        //nothing to do; only validate UI
//    }
//
//    @Then("I should see a form to be filled.")
//    public void i_should_see_a_form_to_be_filled() {
//        //nothing to do; only validate UI
//    }
//
//    @When("I execute the previous steps")
//    public void i_execute_the_previous_steps() {
//        //nothing to do; only validate UI
//    }
//
//    @When("I fill in and submit the form,")
//    public void i_fill_in_and_submit_the_form() throws ParseException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("name", "test");
//        jsonBody.put("duration", 600);
//        
//        HttpEntity entity = new HttpEntity(jsonBody, headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/session", HttpMethod.POST, entity, String.class);
//        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
//        currentSessionId=(long)json.get("id");
//        assertEquals(result.getStatusCodeValue(),200);
//    }
//
//    @Then("I should see a message informing me about the success\\/failure of the operation")
//    public void i_should_see_a_message_informing_me_about_the_success_failure_of_the_operation() {
//        //nothing to do; only validate UI
//    }
//
//    @Then("\\(if successful) I should be redirected to the session lobby.")
//    public void if_successful_I_should_be_redirected_to_the_session_lobby() {
//        //nothing to do; only validate UI
//    }
//
//    @Then("I should see the game session lobby")
//    public void i_should_see_the_game_session_lobby() {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {}
//        List<DBSession> sessions = sr.getSessionById(currentSessionId);
//        assertEquals(sessions.size(), 1);
//        DBSession session = sessions.get(0);
//        assertEquals(session.getIsAvailable(), true);
//        List<DBUser> users = ur.getUserByUsername(currentUsername);
//        assertEquals(users.size(),1);
//        DBUser user = users.get(0);
//        assertThat(session.getPlayers().contains(user));
//    }
//
//    @Then("I should be the admin.")
//    public void i_should_be_the_admin() {
//        List<DBSession> sessions = sr.getSessionById(currentSessionId);
//        assertEquals(sessions.size(), 1);
//        DBSession session = sessions.get(0);
//        assertEquals(session.getIsAvailable(), true);
//        List<DBUser> users = ur.getUserByUsername(currentUsername);
//        assertEquals(users.size(),1);
//        DBUser user = users.get(0);
//        assertThat(session.getCreator().equals(user));
//    }
//
//    @Given("I see the game session I want in the game session list,")
//    public void i_see_the_game_session_I_want_in_the_game_session_list() throws ParseException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("name", "test");
//        jsonBody.put("duration", 600);
//        
//        HttpEntity entity = new HttpEntity(jsonBody, headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/session", HttpMethod.POST, entity, String.class);
//        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
//        currentSessionId=(long)json.get("id");
//        assertEquals(result.getStatusCodeValue(),200);
//        
//        result = restTemplate.exchange(server+randomServerPort+"/session", HttpMethod.GET, entity, String.class);
//        json = (JSONObject)new JSONParser().parse(result.getBody());
//        assertThat(((JSONObject)json.get("sessions")).containsKey(String.valueOf(currentSessionId)));
//    }
//
//    @When("I click the join button of that session,")
//    public void i_click_the_join_button_of_that_session() throws ParseException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("action", "join");
//        
//        HttpEntity entity = new HttpEntity(jsonBody, headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/session/"+currentSessionId, HttpMethod.POST, entity, String.class);
//        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
//        assertEquals(result.getStatusCodeValue(),200);
//    }
//    
//    @When("I click the invite user as friend button")
//    public void i_click_the_invite_user_as_friend_button() {
//        //nothing to do; only validate UI
//    }
//
//    @Then("I should see a form that allows the user to insert another user's username.")
//    public void i_should_see_a_form_that_allows_the_user_to_insert_another_user_s_username() {
//        //nothing to do; only validate UI
//    }
//
//    @Given("I'm in the invite user as friend form")
//    public void i_m_in_the_invite_user_as_friend_form() {
//        //nothing to do; only validate UI
//    }
//
//    @When("I click the send notification button,")
//    public void i_click_the_send_notification_button() throws ParseException {
//        currentFriendname=currentUsername+"friend";
//        UserData ud = new UserData();
//        ud.setUsername(currentFriendname);
//        ud.setEmail(currentFriendname+"@mail.com");
//        ud.setPassword("123".toCharArray());
//        us.registerUser(ud);
//        
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        HttpEntity entity = new HttpEntity(headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/friends/"+currentFriendname, HttpMethod.GET, entity, String.class);
//        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
//        assertEquals(result.getStatusCodeValue(),200);
//    }
//
//    @Then("I should be notified that a message with my friendship request was sent.")
//    public void i_should_be_notified_that_a_message_with_my_friendship_request_was_sent() {
//        //nothing to do; only validate UI
//    }
//
//    @When("I'm using the platform and other user accepts my friendship request,")
//    public void i_m_using_the_platform_and_other_user_accepts_my_friendship_request() throws ParseException {
//        currentFriendname=currentUsername+"friend";
//        UserData ud = new UserData();
//        ud.setUsername(currentFriendname);
//        ud.setEmail(currentFriendname+"@mail.com");
//        ud.setPassword("123".toCharArray());
//        us.registerUser(ud);
//        
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        HttpEntity entity = new HttpEntity(headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/friends/"+currentFriendname, HttpMethod.GET, entity, String.class);
//        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
//        assertEquals(result.getStatusCodeValue(),200);
//        
//        headers = new HttpHeaders();
//        headers.setBasicAuth(currentFriendname, "123");
//        
//        entity = new HttpEntity(headers);
//        
//        result = restTemplate.exchange(server+randomServerPort+"/friends/"+currentUsername, HttpMethod.GET, entity, String.class);
//        json = (JSONObject)new JSONParser().parse(result.getBody());
//        assertEquals(result.getStatusCodeValue(),200);
//    }
//
//    @Then("I should be notified that that user has accepted my friendship request.")
//    public void i_should_be_notified_that_that_user_has_accepted_my_friendship_request() throws ParseException {
//        Properties properties = new Properties();
//        properties.put("bootstrap.servers", System.getProperty("KAFKA_BOOTSTRAP_SERVERS"));
//        properties.put("group.id", "es_g54_group_test"+currentUsername);
//        properties.put("auto.offset.reset", "latest");
//        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
//        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        KafkaConsumer consumer = new KafkaConsumer<Integer,String>(properties);
//        consumer.subscribe(Arrays.asList(currentUsername));
//        
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {}
//
//        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(100));
//        for (ConsumerRecord<Integer,String> record : records){
//            JSONObject json = (JSONObject) new JSONParser().parse(record.value());
//            assertThat(json.containsKey("friendInvite"));
//            JSONObject invite = (JSONObject)json.get("friendInvite");
//            assertEquals(invite.get("user"), currentFriendname);
//        }
//        consumer.commitSync();
//        consumer.close();
//    }
//
//    @Given("I see a friend I want to invite at the friends list,")
//    public void i_see_a_friend_I_want_to_invite_at_the_friends_list() {
//        //nothing to do; only validate UI
//    }
//
//    @When("I click the invite button related to that friend")
//    public void i_click_the_invite_button_related_to_that_friend() throws ParseException {
//        currentFriendname=currentUsername+"friend";
//        UserData ud = new UserData();
//        ud.setUsername(currentFriendname);
//        ud.setEmail(currentFriendname+"@mail.com");
//        ud.setPassword("123".toCharArray());
//        us.registerUser(ud);
//        
//        
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        HttpEntity entity = new HttpEntity(headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/friends/"+currentFriendname+"/session/"+currentSessionId, HttpMethod.GET, entity, String.class);
//        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
//        assertEquals(result.getStatusCodeValue(),200);
//    }
//
//    @Then("I should be notified that a message with the invitation was sent to my friend.")
//    public void i_should_be_notified_that_a_message_with_the_invitation_was_sent_to_my_friend() {
//        //nothing to do; only validate UI
//    }
//    
//    @Given("I am in a game session,")
//    public void i_am_in_a_game_session() throws ParseException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(currentUsername, "123");
//        
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("name", "test");
//        jsonBody.put("duration", 600);
//        
//        HttpEntity entity = new HttpEntity(jsonBody, headers);
//        
//        ResponseEntity<String> result = restTemplate.exchange(server+randomServerPort+"/session", HttpMethod.POST, entity, String.class);
//        JSONObject json = (JSONObject)new JSONParser().parse(result.getBody());
//        currentSessionId=(long)json.get("id");
//        assertEquals(result.getStatusCodeValue(),200);
//        
//        jsonBody = new JSONObject();
//
//        entity = new HttpEntity(jsonBody, headers);
//        
//        result = restTemplate.exchange(server+randomServerPort+"/session/"+currentSessionId, HttpMethod.PUT, entity, String.class);
//        assertEquals(result.getStatusCodeValue(),200);
//    }
//
//    @When("I raise my right hand above my head")
//    public void i_raise_my_right_hand_above_my_head() {
//        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
//                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
//                + "\"LeftElbow\": [1751.346,713.7789,0.0], \"RightElbow\": [0.0,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [0.0,1193.861,0.0], "
//                + "\"RightHand\": [0.0,1908.936,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
//                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
//        kt.send("esp54_"+currentSessionId, jsonBody);
//    }
//
//    @Then("I should be notified that a message was send to the admin")
//    public void i_should_be_notified_that_a_message_was_send_to_the_admin() throws ParseException {
//        Properties properties = new Properties();
//        properties.put("bootstrap.servers", System.getProperty("KAFKA_BOOTSTRAP_SERVERS"));
//        properties.put("group.id", "es_g54_group_test"+currentUsername);
//        properties.put("auto.offset.reset", "latest");
//        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
//        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        KafkaConsumer consumer = new KafkaConsumer<Integer,String>(properties);
//        consumer.subscribe(Arrays.asList("esp54_"+currentSessionId));
//        
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {}
//
//        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(100));
////        assertEquals(2, records.count());
//        for (ConsumerRecord<Integer,String> record : records){
//            JSONObject json = (JSONObject) new JSONParser().parse(record.value());
//            if (json.get("positions") != null) {
//                continue;
//            }
//            assertEquals(json.get("username"), currentUsername);
//            assertEquals(json.get("msg"), "Admin notified.");
//        }
//        consumer.commitSync();
//        consumer.close();
//    }
//
//    @When("I raise my left hand above my head")
//    public void i_raise_my_left_hand_above_my_head() {
//        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
//                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
//                + "\"LeftElbow\": [1751.346,713.7789,0.0], \"RightElbow\": [0.0,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [0.0,1908.936,0.0], "
//                + "\"RightHand\": [0.0,1908.936,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
//                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
//        kt.send("esp54_"+currentSessionId, jsonBody);
//    }
//
//    @When("I perform the initial position\\(spread arms)")
//    public void i_perform_the_initial_position_spread_arms() throws ParseException {
//        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
//                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
//                + "\"LeftElbow\": [1751.346,713.7789,0.0], \"RightElbow\": [0.0,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [0.0,1193.861,0.0], "
//                + "\"RightHand\": [0.0,1175.847,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
//                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
//        kt.send("esp54_"+currentSessionId, jsonBody);
//    }
//
//    @Then("I should be recognized by the platform")
//    public void i_should_be_recognized_by_the_platform() throws ParseException, InterruptedException {
//        Properties properties = new Properties();
//        properties.put("bootstrap.servers", System.getProperty("KAFKA_BOOTSTRAP_SERVERS"));
//        properties.put("group.id", "es_g54_group_test"+currentUsername);
//        properties.put("auto.offset.reset", "latest");
//        properties.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
//        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        KafkaConsumer consumer = new KafkaConsumer<Integer,String>(properties);
//        consumer.subscribe(Arrays.asList("esp54_"+currentSessionId));
//        
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {}
//
//        ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(100));
////        assertEquals(2, records.count());
//        for (ConsumerRecord<Integer,String> record : records){
//            JSONObject json = (JSONObject) new JSONParser().parse(record.value());
//            if (json.get("positions") != null) {
//                continue;
//            }
//            assertEquals(json.get("username"), currentUsername);
//            assertEquals(json.get("msg"), "User recognized.");
//        }
//        consumer.commitSync();
//        consumer.close();
//    }
//
//    @Then("if am the last user recognized, the game session should start.")
//    public void if_am_the_last_user_recognized_the_game_session_should_start() {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {}
//        List<DBSession> sessions = sr.getSessionById(currentSessionId);
//        assertEquals(sessions.size(), 1);
//        DBSession session = sessions.get(0);
//        //assertEquals(session.getIsActive(), true); // TODO
//    }
//
//    @When("I perform the stopping position\\(cross arms over head)")
//    public void i_perform_the_stopping_position_cross_arms_over_head() {
//        String jsonBody = "{\"username\": \""+currentUsername+"\", \"positions\": {\"Head\": [2098.635,1708.936,0.0], \"Neck\": [2227.439,1410.903,0.0], \"LeftCollar\": [2252.219,1217.666,0.0], "
//                + "\"Torso\": [2314.787,729.7457,0.0], \"Waist\": [2356.344,278.0999,0.0], \"LeftShoulder\": [1860.994,1193.861,0.0], \"RightShoulder\": [2813.465,1175.847,0.0], "
//                + "\"LeftElbow\": [2098.635,713.7789,0.0], \"RightElbow\": [2198.635,0.0,0.0], \"LeftWrist\": [0.0,0.0,0.0], \"RightWrist\": [0.0,0.0,0.0], \"LeftHand\": [2198.635,1908.936,0.0], "
//                + "\"RightHand\": [2098.635,1908.936,0.0], \"LeftHip\": [0.0,0.0,0.0], \"RightHip\": [2672.045,226.5393,0.0], \"LeftKnee\": [0.0,0.0,0.0], \"RightKnee\": [0.0,0.0,0.0], "
//                + "\"LeftAnkle\": [0.0,0.0,0.0], \"RightAnkle\": [0.0,0.0,0.0]}}";
//        kt.send("esp54_"+currentSessionId, jsonBody);
//    }
//
//    @Then("I should see the game session to be immediately stopped.")
//    public void i_should_see_the_game_session_to_be_immediately_stopped() {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {}
//        List<DBSession> sessions = sr.getSessionById(currentSessionId);
//        assertEquals(sessions.size(), 1);
//        DBSession session = sessions.get(0);
//        //assertEquals(session.getIsActive(), false); // TODO
//        //assertEquals(session.getIsAvailable(), false); // TODO
//    }
//
//}
