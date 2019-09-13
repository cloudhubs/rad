package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.context.SeerRestContext;
import edu.baylor.ecs.seer.lweaver.service.ResourceService;
import javassist.CtClass;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.ArrayList;
import java.util.List;

public class RestDiscoveryService {
    private final ResourceService resourceService;
    private final SeerRestContextService restContextService;

    public RestDiscoveryService() {
        resourceService = new ResourceService(new DefaultResourceLoader());
        restContextService = new SeerRestContextService();
    }

    public List<SeerRestContext> generateSeerRestContexts(String folderPath, String organizationPath) {
        List<SeerRestContext> seerRestContexts = new ArrayList<>();

        List<String> resourcePaths = resourceService.getResourcePaths(folderPath);
        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, organizationPath);
            seerRestContexts.add(restContextService.getSeerRestContext(path, ctClasses));
        }

        return seerRestContexts;
    }
}
