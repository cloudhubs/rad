package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.analyzer.*;
import edu.baylor.ecs.seer.context.SeerRestEntityContext;
import edu.baylor.ecs.seer.model.HttpMethod;
import edu.baylor.ecs.seer.model.RestEntity;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
@AllArgsConstructor
public class RestEntityService {
    private final JaxRsAnalyzer jaxRsAnalyzer;
    private final SpringAnalyzer springAnalyzer;
    private final SpringClientAnalyzer springClientAnalyzer;
    private final SpringClientWrapperAnalyzer springClientWrapperAnalyzer;

    public SeerRestEntityContext getRestEntityContext(List<CtClass> allClasses, String path, Properties properties) {
        SeerRestEntityContext restEntityContext = new SeerRestEntityContext();

        for (CtClass ctClass : allClasses) {
            //restEntityContext.getRestEntities().addAll(jaxRsAnalyzer.getRestEntity(ctClass));
            //restEntityContext.getRestEntities().addAll(springAnalyzer.getRestEntity(ctClass));
            restEntityContext.getRestEntities().addAll(springClientAnalyzer.getRestEntity(ctClass, properties));
            // restEntityContext.getRestEntities().addAll(springClientWrapperAnalyzer.getRestEntity(ctClass));
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

        // find application name, used in eureka discovery
        restEntity.setApplicationName(findApplicationNameProperties(properties));

        // find serverIP and port
        String serverIP = "http://localhost"; // TODO
        String serverPort = findPortFromProperties(properties);

        if (!restEntity.isClient()) { // set server ip and port
            restEntity.setUrl(Helper.mergeUrlPath(serverIP + ":" + serverPort, restEntity.getPath()));
        } else if (restEntity.getUrl() == null) { // set client ip and port from MicroProfile config
            String mpRestUrl = properties.getProperty(restEntity.getClassName() + "/mp-rest/url");
            if (mpRestUrl != null) {
                restEntity.setUrl(Helper.mergeUrlPath(mpRestUrl, restEntity.getPath()));
            }
        }
    }

    private String findPortFromProperties(Properties properties) {
        if (properties == null) return "";

        String port = properties.getProperty("port");
        if (port == null) {
            port = properties.getProperty("server.port");
        }
        return port;
    }

    private String findApplicationNameProperties(Properties properties) {
        if (properties == null) return null;
        return properties.getProperty("spring.application.name");
    }
}
