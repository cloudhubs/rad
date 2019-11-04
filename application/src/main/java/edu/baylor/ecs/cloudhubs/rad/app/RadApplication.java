package edu.baylor.ecs.cloudhubs.rad.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"edu.baylor.ecs.cloudhubs.rad", "edu.baylor.ecs.seer.lweaver.service"})
public class RadApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadApplication.class, args);
    }

}
