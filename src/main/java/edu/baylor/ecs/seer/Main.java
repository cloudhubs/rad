package edu.baylor.ecs.seer;

import edu.baylor.ecs.seer.context.SeerRestContext;
import edu.baylor.ecs.seer.service.RestDiscoveryService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // TODO: Args or API
        String folderPath = "C:\\seer-lab\\cm5";
        String orgPath = "edu/baylor/icpc";

        System.out.println("Started"); // TODO: remove

        RestDiscoveryService discoveryService = new RestDiscoveryService();
        List<SeerRestContext> seerRestContexts = discoveryService.generateSeerRestContexts(folderPath, orgPath);

        // System.out.println(seerRestContexts); // TODO: API response
        System.out.println("done"); // TODO: remove
    }
}
