package edu.baylor.ecs.seer;

import edu.baylor.ecs.seer.context.SeerRestContext;
import edu.baylor.ecs.seer.service.RestDiscoveryService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // TODO: Args or API
        String folderPath = "";
        String orgPath = "";

        // TODO: fix it
        RestDiscoveryService discoveryService = new RestDiscoveryService(null, null);

        List<SeerRestContext> seerRestContexts = discoveryService.generateSeerRestContexts(folderPath, orgPath);

        System.out.println(seerRestContexts); // TODO: API response
    }
}
