package edu.baylor.ecs.seer.analyzer;

import edu.baylor.ecs.seer.entity.HttpMethod;
import edu.baylor.ecs.seer.entity.RestEntity;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpringClientWrapperAnalyzer {
    @AllArgsConstructor
    private static class RestTemplateMethod {
        String restTemplateMethod;
        HttpMethod httpMethod;
    }

    static final RestTemplateMethod[] restTemplateMethods = {
            new RestTemplateMethod("getForObject", HttpMethod.GET),
            new RestTemplateMethod("postForObject", HttpMethod.POST),
            new RestTemplateMethod("deleteForObject", HttpMethod.DELETE),
    };

    static final String restTemplateClass = "org.springframework.web.client.RestTemplate";

    public List<RestEntity> getRestEntity(CtClass ctClass) {
        List<RestEntity> restEntities = new ArrayList<>();

        for (CtMethod ctMethod : ctClass.getMethods()) {
            RestTemplateMethod foundMethod = findCaller(ctMethod);
            if (foundMethod != null) {
                RestEntity restEntity = new RestEntity();
                restEntity.setClient(true);
                restEntity.setHttpMethod(foundMethod.httpMethod);

                // add class and method signatures
                restEntity.setClassName(ctClass.getName());
                restEntity.setMethodName(ctMethod.getName());
                restEntity.setReturnType(Helper.getReturnType(ctMethod));

                restEntities.add(restEntity);
            }
        }
        return restEntities;
    }

    private RestTemplateMethod findCaller(CtMethod method) {
        // Instrument the method to pull out the method calls
        // Then filter method calls by methodName and className
        final RestTemplateMethod[] foundMethod = {null};
        try {
            method.instrument(
                    new ExprEditor() {
                        public void edit(MethodCall m) {
                            for (RestTemplateMethod targetMethod : restTemplateMethods) {
                                if (m.getClassName().equals(restTemplateClass) && m.getMethodName().equals(targetMethod.restTemplateMethod)) {
                                    foundMethod[0] = targetMethod;
                                    break;
                                }
                            }
                        }
                    }
            );
        } catch (CannotCompileException e) {
            System.err.println(e.toString());
        }

        return foundMethod[0];
    }
}
