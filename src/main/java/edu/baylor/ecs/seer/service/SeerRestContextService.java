package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.analyzer.JaxRsAnalyzer;
import edu.baylor.ecs.seer.analyzer.SpringAnalyzer;
import edu.baylor.ecs.seer.context.SeerRestContext;
import edu.baylor.ecs.seer.entity.RestEndpoint;
import javassist.CtClass;

import java.util.List;

public class SeerRestContextService {
    private final JaxRsAnalyzer jaxRsAnalyzer = new JaxRsAnalyzer();
    private final SpringAnalyzer springAnalyzer = new SpringAnalyzer();

    public SeerRestContext getSeerRestContext(String resourcePath, List<CtClass> allClasses) {
        SeerRestContext restContext = new SeerRestContext();

        for (CtClass ctClass : allClasses) {
            System.out.println("### ClassName: " + ctClass.getName()); // TODO: log
            List<RestEndpoint> endpoints = jaxRsAnalyzer.getRestEndpoint(resourcePath, ctClass);
            if (endpoints.isEmpty()) { // if JAX RS annotation not found, try spring annotations
                restContext.getRestEndpoints().addAll(springAnalyzer.getRestEndpoint(resourcePath, ctClass));
            }
        }

        return restContext;
    }


}
