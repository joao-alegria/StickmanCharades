package pt.ua.deti.es.g54;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class SessionCommandsApplication {

    private static final Logger logger = LoggerFactory.getLogger(SessionCommandsApplication.class);
    

    public static void main(String[] args) {
        List<String> requiredEnvVariables = Arrays.asList(
                "KAFKA_BOOTSTRAP_SERVERS",
                "LOGGING_HOST", "LOGGING_PORT", "LOGGING_USER", "LOGGING_PASSWORD", "LOGGING_INDEX",
                "MONITORING_HOST", "MONITORING_PORT"
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
        
        if(System.getenv().get("KAFKA_BOOTSTRAP_SERVERS")!=null && (!System.getenv().get("KAFKA_BOOTSTRAP_SERVERS").equals(""))){
            Constants.KAFKA_BOOTSTRAP_SERVER=System.getenv().get("KAFKA_BOOTSTRAP_SERVERS");
        }

        logger.info("Launching application");
        SpringApplication.run(SessionCommandsApplication.class, args);
    }

}
