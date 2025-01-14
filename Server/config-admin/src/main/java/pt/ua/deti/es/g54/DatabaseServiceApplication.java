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
public class DatabaseServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceApplication.class);


	public static void main(String[] args) {
		List<String> requiredEnvVariables = Arrays.asList(
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

        logger.info("Launching application");
		SpringApplication.run(DatabaseServiceApplication.class, args);
	}

}
