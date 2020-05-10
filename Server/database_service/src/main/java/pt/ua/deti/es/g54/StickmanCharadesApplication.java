package pt.ua.deti.es.g54;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableScheduling
public class StickmanCharadesApplication {

	private static Logger logger = LoggerFactory.getLogger(StickmanCharadesApplication.class);

	public static void main(String[] args) throws InterruptedException, ExecutionException {
            List<String> requiredEnvVariables = Arrays.asList(
                            "KAFKA_BOOTSTRAP_SERVERS",
                            "PERSISTENCE_HOST", "PERSISTENCE_PORT", "PERSISTENCE_DB", "PERSISTENCE_USER", "PERSISTENCE_PASSWORD"
            );

            Map<String, String> env = System.getenv();
            boolean variablesMissing = false;
            for (String envVariable: requiredEnvVariables) {
                    if (env.get(envVariable) == null) {
                            logger.error(String.format("Environment variable '%s' wasn't defined", envVariable));
                            variablesMissing = true;
                    }
            }

            if (variablesMissing) {
                    System.exit(1);
            }

            System.setProperty("KAFKA_BOOTSTRAP_SERVERS", System.getenv().get("KAFKA_BOOTSTRAP_SERVERS"));
            
            SpringApplication.run(StickmanCharadesApplication.class, args);
	}

//    @Bean
//    public ApplicationRunner runner(KafkaAdmin kafkaAdmin) {
//        return args -> {
//            AdminClient admin = AdminClient.create(kafkaAdmin.getConfig());
//            List<NewTopic> topics = new ArrayList<>();
//            // build list
//            topics.add(new NewTopic("esp54_databaseServiceTopic",1,(short)1));
//            Collection<TopicListing> topicListings = admin.listTopics().listings().get();
//            for(TopicListing tl: topicListings){
//                if(tl.name().equals("esp54_databaseServiceTopic")){
//                    return;
//                }
//            }
//            admin.createTopics(topics).all().get();
//        };
//    }
}
