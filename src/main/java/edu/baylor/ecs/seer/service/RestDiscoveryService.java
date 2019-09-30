package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.analyzer.Helper;
import edu.baylor.ecs.seer.analyzer.JaxRsAnalyzer;
import edu.baylor.ecs.seer.analyzer.SpringAnalyzer;
import edu.baylor.ecs.seer.context.RadRequestContext;
import edu.baylor.ecs.seer.context.RadResponseContext;
import edu.baylor.ecs.seer.context.SeerRestEntityContext;
import edu.baylor.ecs.seer.context.SeerRestFlowContext;
import edu.baylor.ecs.seer.entity.HttpMethod;
import edu.baylor.ecs.seer.entity.RestEntity;
import edu.baylor.ecs.seer.entity.RestFlow;
import edu.baylor.ecs.seer.lweaver.service.ResourceService;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@AllArgsConstructor
@Service
public class RestDiscoveryService {
    private final ResourceService resourceService;
    private final JaxRsAnalyzer jaxRsAnalyzer;
    private final SpringAnalyzer springAnalyzer;

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

            SeerRestEntityContext restEntityContext = getRestEntityContext(ctClasses, path, properties);
            radResponseContext.getRestEntityContexts().add(restEntityContext);
        }

        SeerRestFlowContext restFlowContext = getRestFlowContext(radResponseContext.getRestEntityContexts());
        radResponseContext.setRestFlowContext(restFlowContext);

        return radResponseContext;
    }

    public String findPortFromProperties(Properties properties) {
        if (properties == null) return "";

        String port = properties.getProperty("port");
        if (port == null) {
            port = properties.getProperty("server.port");
        }
        return port;
    }

    public SeerRestEntityContext getRestEntityContext(List<CtClass> allClasses, String path, Properties properties) {
        SeerRestEntityContext restEntityContext = new SeerRestEntityContext();

        for (CtClass ctClass : allClasses) {
            restEntityContext.getRestEntities().addAll(jaxRsAnalyzer.getRestEntity(ctClass));
            restEntityContext.getRestEntities().addAll(springAnalyzer.getRestEntity(ctClass));
        }

        for (RestEntity restEntity : restEntityContext.getRestEntities()) {
            populateDefaultProperties(restEntity, path, properties);
        }

        return restEntityContext;
    }

    private void populateDefaultProperties(RestEntity restEntity, String path, Properties properties) {
        restEntity.setResourcePath(path);
        if (restEntity.getPath() == null) {
            restEntity.setPath("/");
        }
        if (restEntity.getHttpMethod() == null) {
            restEntity.setHttpMethod(HttpMethod.GET);
        }

        // find serverIP and port
        String serverIP = "http://localhost"; // TODO
        String serverPort = findPortFromProperties(properties);

        if (!restEntity.isClient()) { // set server ip and port
            restEntity.setUrl(Helper.mergeUrlPath(serverIP + ":" + serverPort, restEntity.getPath()));
        } else { // set client ip and port from MicroProfile config
            String mpRestUrl = properties.getProperty(restEntity.getClassName() + "/mp-rest/url");
            if (mpRestUrl != null) {
                restEntity.setUrl(Helper.mergeUrlPath(mpRestUrl, restEntity.getPath()));
            }
        }
    }

    public SeerRestFlowContext getRestFlowContext(List<SeerRestEntityContext> restEntityContexts) {
        List<RestEntity> serverEntities = new ArrayList<>();
        List<RestEntity> clientEntities = new ArrayList<>();

        for (SeerRestEntityContext restEntityContext : restEntityContexts) {
            for (RestEntity restEntity : restEntityContext.getRestEntities()) {
                if (restEntity.isClient()) clientEntities.add(restEntity);
                else serverEntities.add(restEntity);
            }
        }

        SeerRestFlowContext restFlowContext = new SeerRestFlowContext();

        // populate RestFlow
        for (RestEntity restClientEntity : clientEntities) {
            for (RestEntity restServerEntity : serverEntities) {
                // match url and http method
                if (restClientEntity.getHttpMethod() == restServerEntity.getHttpMethod() &&
                        Helper.matchUrl(restClientEntity.getUrl(), restServerEntity.getUrl())) {

                    RestFlow restFlow = new RestFlow();

                    restFlow.setResourcePath(restClientEntity.getResourcePath());
                    restFlow.setClassName(restClientEntity.getClassName());
                    restFlow.setMethodName(restClientEntity.getMethodName());

                    restFlow.setChildren(new ArrayList<>());
                    restFlow.getChildren().add(restServerEntity);

                    restFlowContext.getRestFlows().add(restFlow);
                }
            }
        }

        return restFlowContext;
    }
}
