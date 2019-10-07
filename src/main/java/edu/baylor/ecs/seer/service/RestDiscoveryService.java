package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.context.RadRequestContext;
import edu.baylor.ecs.seer.context.RadResponseContext;
import edu.baylor.ecs.seer.context.SeerRestEntityContext;
import edu.baylor.ecs.seer.context.SeerRestFlowContext;
import edu.baylor.ecs.seer.lweaver.service.ResourceService;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.Set;

@AllArgsConstructor
@Service
public class RestDiscoveryService {
    private final ResourceService resourceService;
    private final RestEntityService restEntityService;
    private final RestFlowService restFlowService;

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

            SeerRestEntityContext restEntityContext = restEntityService.getRestEntityContext(ctClasses, path, properties);
            radResponseContext.getRestEntityContexts().add(restEntityContext);
        }

        SeerRestFlowContext restFlowContext = restFlowService.getRestFlowContext(radResponseContext.getRestEntityContexts());
        radResponseContext.setRestFlowContext(restFlowContext);

        return radResponseContext;
    }
}
