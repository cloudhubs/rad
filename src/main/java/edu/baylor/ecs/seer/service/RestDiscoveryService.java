package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.context.SeerRestContext;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

public class RestDiscoveryService {
    private final ResourceService resourceService;
    private final SeerRestContextService restContextService;

    public RestDiscoveryService(ResourceService resourceService, SeerRestContextService restContextService) {
        this.resourceService = resourceService;
        this.restContextService = restContextService;
    }

    public List<SeerRestContext> generateSeerRestContexts(String folderPaths, String organizationPath) {
        List<SeerRestContext> seerRestContexts = new ArrayList<>();

        List<String> resourcePaths = resourceService.getResourcePaths(folderPaths);
        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, organizationPath);
            seerRestContexts.add(restContextService.getSeerRestContext(ctClasses));
        }

        return seerRestContexts;
    }
}
