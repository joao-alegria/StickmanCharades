package pt.ua.deti.es.g54;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;

@SpringBootApplication
@EnableScheduling
public class StickmanCharadesApplication {

    private static Logger logger = LoggerFactory.getLogger(StickmanCharadesApplication.class);

    public static void main(String[] args) {
        List<String> requiredEnvVariables = Arrays.asList(
            "KAFKA_BOOTSTRAP_SERVERS"
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

        logger.info("Launching application");
        SpringApplication.run(StickmanCharadesApplication.class, args);
    }
        
    @Bean
    public ApplicationRunner runner(KafkaAdmin kafkaAdmin) {
        return args -> {
            AdminClient admin = AdminClient.create(kafkaAdmin.getConfig());
            List<NewTopic> topics = new ArrayList<>();
            // build list
            topics.add(new NewTopic("esp54_commandsServiceTopic",1,(short)1));
            Collection<TopicListing> topicListings = admin.listTopics().listings().get();
            for(TopicListing tl: topicListings){
                if(tl.name().equals("esp54_commandsServiceTopic")){
                    return;
                }
            }
            admin.createTopics(topics).all().get();
        };
    }

}
