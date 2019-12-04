package edu.baylor.ecs.cloudhubs.rad.service;

import edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.RadResponseContext;
import edu.baylor.ecs.cloudhubs.rad.context.SeerRestEntityContext;
import edu.baylor.ecs.cloudhubs.rad.context.SeerRestFlowContext;
import edu.baylor.ecs.cloudhubs.rad.graph.GVGenerator;
import edu.baylor.ecs.seer.lweaver.service.ResourceService;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * This class constructs a {@link edu.baylor.ecs.cloudhubs.rad.context.RadResponseContext}.
 * It takes a {@link edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext} as input.
 *
 * @author Dipta Das
 */

@AllArgsConstructor
@Service
public class RestDiscoveryService {
    private final ResourceService resourceService;
    private final RestEntityService restEntityService;
    private final RestFlowService restFlowService;

    public RestDiscoveryService() {
        this.resourceService = new ResourceService(new DefaultResourceLoader());
        this.restEntityService = new RestEntityService();
        this.restFlowService = new RestFlowService();
    }

    public RadResponseContext generateRadResponseContext(RadRequestContext request) {
        RadResponseContext radResponseContext = new RadResponseContext();
        radResponseContext.setRequest(request);

        List<String> resourcePaths = resourceService.getResourcePaths(request.getPathToCompiledMicroservices());
        for (String path : resourcePaths) {
            List<CtClass> ctClasses = resourceService.getCtClasses(path, request.getOrganizationPath());

            Set<Properties> propertiesSet = resourceService.getProperties(path, request.getOrganizationPath());
            Properties properties;
            if (propertiesSet.size() > 0) {
                properties = propertiesSet.iterator().next();
            } else properties = null;

            // print the properties for debug
            // Helper.dumpProperties(properties, path);

            SeerRestEntityContext restEntityContext = restEntityService.getRestEntityContext(ctClasses, path, properties);
            radResponseContext.getRestEntityContexts().add(restEntityContext);
        }

        SeerRestFlowContext restFlowContext = restFlowService.getRestFlowContext(radResponseContext.getRestEntityContexts());
        radResponseContext.setRestFlowContext(restFlowContext);

        GVGenerator.generate(radResponseContext);

        return radResponseContext;
    }
}
