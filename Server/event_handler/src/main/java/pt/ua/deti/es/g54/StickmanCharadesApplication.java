package pt.ua.deti.es.g54;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class StickmanCharadesApplication {

	private static final Logger logger = LoggerFactory.getLogger(StickmanCharadesApplication.class);

	public static void main(String[] args) {
		Map<String, String> env = System.getenv();
		if (env.get("KAFKA_BOOTSTRAP_SERVERS") == null) {
			logger.error("Environment variable KAFKA_BOOTSTRAP_SERVERS wasn't defined");
			System.exit(1);
		}

		Constants.BOOTSTRAP_SERVERS = env.get("KAFKA_BOOTSTRAP_SERVERS");

		logger.info("Launching application");
		SpringApplication.run(StickmanCharadesApplication.class, args);
	}

}
