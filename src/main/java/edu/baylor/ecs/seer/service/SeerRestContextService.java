package edu.baylor.ecs.seer.service;

import edu.baylor.ecs.seer.context.SeerRestContext;
import edu.baylor.ecs.seer.entity.RestEndpoint;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;

public class SeerRestContextService {
    public SeerRestContext getSeerRestContext(List<CtClass> allClasses) {
        SeerRestContext restContext = new SeerRestContext();

        // TODO: analysis
        for (CtClass ctClass : allClasses) {
            restContext.getRestEndpoints().addAll(analyseClass(ctClass));
        }

        return restContext;
    }

    private List<RestEndpoint> analyseClass(CtClass ctClass) {
        List<RestEndpoint> restEndpoints = new ArrayList<>();

        // get path annotation specified in class level
        String path;
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals("javax.ws.rs.Path")) {
                    path = annotation.getMemberValue("value").toString();
                    System.out.println("path-class " + path);
                }
            }
        }

        for (CtMethod ctMethod : ctClass.getMethods()) {
            restEndpoints.add(analyseMethod(ctMethod));
        }

        return restEndpoints;
    }

    private RestEndpoint analyseMethod(CtMethod ctMethod) {
        RestEndpoint restEndpoint = new RestEndpoint();

        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals("javax.ws.rs.Path")) {
                    String path = annotation.getMemberValue("value").toString();
                    System.out.println("path " + path);
                }
            }
        }

        LocalVariableAttribute variableAttribute = (LocalVariableAttribute) ctMethod.getMethodInfo().getAttribute(LocalVariableAttribute.tag);
        if (variableAttribute != null) {
            System.out.println(variableAttribute.nameIndex(0));
        }

        return restEndpoint;
    }

    // TODO: analyse variables for params
}
