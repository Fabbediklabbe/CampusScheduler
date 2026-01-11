package se.ltu.campusscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import se.ltu.campusscheduler.canvas.CanvasProperties;

@SpringBootApplication
@EnableConfigurationProperties(CanvasProperties.class)
public class CampusschedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusschedulerApplication.class, args);
	}

}
