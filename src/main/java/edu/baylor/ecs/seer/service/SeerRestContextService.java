package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.analyzer.JaxRsAnalyzer;
import edu.baylor.ecs.seer.context.SeerRestContext;
import javassist.CtClass;

import java.util.List;

public class SeerRestContextService {
    private final JaxRsAnalyzer jaxRsAnalyzer = new JaxRsAnalyzer();

    public SeerRestContext getSeerRestContext(String resourcePath, List<CtClass> allClasses) {
        SeerRestContext restContext = new SeerRestContext();

        for (CtClass ctClass : allClasses) {
            // System.out.println("ClassName: " + ctClass.getName()); // TODO: log
            restContext.getRestEndpoints().addAll(jaxRsAnalyzer.getRestEndpoint(resourcePath, ctClass));
        }

        return restContext;
    }


}
