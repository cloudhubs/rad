package edu.baylor.ecs.seer.analyzer;

import edu.baylor.ecs.seer.entity.HttpMethod;
import edu.baylor.ecs.seer.entity.Param;
import edu.baylor.ecs.seer.entity.RestEndpoint;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;

public class JaxRsAnalyzer {
    public List<RestEndpoint> getRestEndpoint(String resourcePath, CtClass ctClass) {
        List<RestEndpoint> restEndpoints = new ArrayList<>();

        // get path annotation specified in class level
        String path = "";
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals("javax.ws.rs.Path")) { // TODO: use constant
                    path = annotation.getMemberValue("value").toString();
                }
            }
        }

        for (CtMethod ctMethod : ctClass.getMethods()) {
            RestEndpoint restEndpoint = analyseMethod(ctMethod);
            if (restEndpoint != null) {
                // append class level path
                restEndpoint.setPath(mergePaths(path, restEndpoint.getPath()));

                // add resource, class and method signatures
                restEndpoint.setResourcePath(resourcePath);
                restEndpoint.setClassName(ctClass.getName());
                restEndpoint.setMethodName(ctMethod.getName());

                System.out.println(restEndpoint); // TODO: log
                restEndpoints.add(restEndpoint);
            }
        }

        return restEndpoints;
    }

    private RestEndpoint analyseMethod(CtMethod ctMethod) {
        RestEndpoint restEndpoint = new RestEndpoint();

        boolean isRestHandlerMethod = false;

        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                boolean isRestAnnotation = true;

                if (annotation.getTypeName().equals("javax.ws.rs.Path")) { // TODO: use constant
                    restEndpoint.setPath(annotation.getMemberValue("value").toString());
                } else if (annotation.getTypeName().equals("javax.ws.rs.Produces")) { // TODO: use constant
                    restEndpoint.setProduceType(annotation.getMemberValue("value").toString());
                } else if (annotation.getTypeName().equals("javax.ws.rs.Consumes")) { // TODO: use constant
                    restEndpoint.setConsumeType(annotation.getMemberValue("value").toString());
                } else if (annotation.getTypeName().equals("javax.ws.rs.Get")) { // TODO: use constant
                    restEndpoint.setHttpMethod(HttpMethod.GET);
                } else if (annotation.getTypeName().equals("javax.ws.rs.Post")) { // TODO: use constant
                    restEndpoint.setHttpMethod(HttpMethod.POST);
                } else { // not JAX-RS annotation
                    isRestAnnotation = false;
                }
                // TODO: include all HTTP methods and use annotationToHttpMethod()

                // true if at least one JAX-RS annotation found
                isRestHandlerMethod = isRestHandlerMethod || isRestAnnotation;
            }
        }

        // if it is not a rest handler method, don't do further parameter analysis
        if (!isRestHandlerMethod) {
            return null;
        }

        // a class annotated with: @Path("widgets/{id}")
        // can have methods annotated whose arguments are annotated with @PathParam("id")

        ParameterAnnotationsAttribute parameterAnnotationsAttribute = (ParameterAnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
        if (parameterAnnotationsAttribute != null) {
            Annotation[][] annotationsList = parameterAnnotationsAttribute.getAnnotations();
            for (Annotation[] annotations : annotationsList) {
                String defaultValue = "";
                Param pathParam = null, queryParam = null, formParam = null;

                for (Annotation annotation : annotations) {
                    if (annotation.getTypeName().equals("javax.ws.rs.PathParam")) { // TODO: use constant
                        pathParam = new Param(annotation.getMemberValue("value").toString());
                    } else if (annotation.getTypeName().equals("javax.ws.rs.QueryParam")) { // TODO: use constant
                        queryParam = new Param(annotation.getMemberValue("value").toString());
                    } else if (annotation.getTypeName().equals("javax.ws.rs.FormParam")) { // TODO: use constant
                        formParam = new Param(annotation.getMemberValue("value").toString());
                    } else if (annotation.getTypeName().equals("javax.ws.rs.DefaultValue")) { // TODO: use constant
                        defaultValue = annotation.getMemberValue("value").toString();
                    }
                }

                if (pathParam != null) {
                    pathParam.setDefaultValue(defaultValue);
                    restEndpoint.addPathParam(pathParam);
                }
                if (queryParam != null) {
                    queryParam.setDefaultValue(defaultValue);
                    restEndpoint.addQueryParam(queryParam);
                }
                if (formParam != null) {
                    formParam.setDefaultValue(defaultValue);
                    restEndpoint.addFormParam(formParam);
                }
                // TODO: headerParam, cookieParam, matrixParam
            }
        }

        return restEndpoint;
    }

    private String mergePaths(String classPath, String methodPath) {
        // remove quotations and add slash
        classPath = addSlash(removeQuotations(classPath));
        methodPath = addSlash(removeQuotations(methodPath));

        // merge, remove double slash and add quotations
        return addQuotations(removeMultipleSlashes(classPath + methodPath));
    }

    private String removeQuotations(String str) {
        return str.replaceAll("\"", "");
    }

    private String addQuotations(String str) {
        return "\"" + str + "\"";
    }

    private String addSlash(String str) {
        return "/" + str;
    }

    private String removeMultipleSlashes(String str) {
        return str.replaceAll("[/]+", "/");
    }
}
