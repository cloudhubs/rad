package edu.baylor.ecs.cloudhubs.rad.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "edu.baylor.ecs.cloudhubs.rad")
public class RadApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadApplication.class, args);
    }

}
