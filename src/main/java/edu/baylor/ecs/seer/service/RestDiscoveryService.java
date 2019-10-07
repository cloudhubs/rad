package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.analyzer.*;
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
    private final SpringClientAnalyzer springClientAnalyzer;
    private final SpringClientWrapperAnalyzer springClientWrapperAnalyzer;

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
            // springClientAnalyzer.find(ctClass, "getForObject", "org.springframework.web.client.RestTemplate");
            restEntityContext.getRestEntities().addAll(springClientWrapperAnalyzer.getRestEntity(ctClass));
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
        restFlowContext.getRestFlows().addAll(getRestFlows(serverEntities, clientEntities));
        restFlowContext.getPossibleRestFlows().addAll(getPossibleRestFlows(serverEntities, clientEntities));

        return restFlowContext;
    }

    public List<RestFlow> getRestFlows(List<RestEntity> serverEntities, List<RestEntity> clientEntities) {
        List<RestFlow> restFlows = new ArrayList<>();

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

                    if (restFlow.getServers() == null) restFlow.setServers(new ArrayList<>());
                    restFlow.getServers().add(restServerEntity);

                    restFlows.add(restFlow);
                }
            }
        }
        return restFlows;
    }

    public List<RestFlow> getPossibleRestFlows(List<RestEntity> serverEntities, List<RestEntity> clientEntities) {
        List<RestFlow> restFlows = new ArrayList<>();

        // populate RestFlow
        for (RestEntity restClientEntity : clientEntities) {
            for (RestEntity restServerEntity : serverEntities) {
                // match return type and http method
                if (restClientEntity.getHttpMethod() == restServerEntity.getHttpMethod() &&
                        restClientEntity.getReturnType() != null &&
                        restServerEntity.getReturnType() != null &&
                        restClientEntity.getReturnType().equals(restServerEntity.getReturnType())) {

                    RestFlow restFlow = new RestFlow();

                    restFlow.setResourcePath(restClientEntity.getResourcePath());
                    restFlow.setClassName(restClientEntity.getClassName());
                    restFlow.setMethodName(restClientEntity.getMethodName());

                    if (restFlow.getPossibleServers() == null) restFlow.setPossibleServers(new ArrayList<>());
                    restFlow.getPossibleServers().add(restServerEntity);

                    restFlows.add(restFlow);
                }
            }
        }

        return restFlows;
    }
}
