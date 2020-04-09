package es_g54;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StickmanCharadesApplication {

	public static void main(String[] args) {
		SpringApplication.run(StickmanCharadesApplication.class, args);
	}

}
