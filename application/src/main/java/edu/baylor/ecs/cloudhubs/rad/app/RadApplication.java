package edu.baylor.ecs.cloudhubs.rad.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class is the {@link SpringBootApplication} runner for the RAD Application.
 *
 * @author Dipta Das
 */

@SpringBootApplication(scanBasePackages = {"edu.baylor.ecs.cloudhubs.rad"})
public class RadApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadApplication.class, args);
    }

}
