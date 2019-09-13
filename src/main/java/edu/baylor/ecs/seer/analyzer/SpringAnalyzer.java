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

public class SpringAnalyzer {
    public List<RestEndpoint> getRestEndpoint(String resourcePath, CtClass ctClass) {
        List<RestEndpoint> restEndpoints = new ArrayList<>();

        if (!isController(ctClass)) { // not a controller, don't do further analysis
            return restEndpoints;
        }

        // get annotation specified in class level
        String path = null, produces = null, consumes = null;
        HttpMethod method = null;

        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals("org.springframework.web.bind.annotation.RequestMapping")) { // TODO: use constant
                    if (annotation.getMemberValue("value") != null) {
                        path = annotation.getMemberValue("value").toString();
                    }
                    if (annotation.getMemberValue("path") != null) { // path is alias for value
                        path = annotation.getMemberValue("path").toString();
                    }
                    if (annotation.getMemberValue("method") != null) { // path is alias for value
                        method = annotationToHttpMethod(annotation.getMemberValue("method").toString());
                    }
                    if (annotation.getMemberValue("produces") != null) { // path is alias for value
                        produces = annotation.getMemberValue("produces").toString();
                    }
                    if (annotation.getMemberValue("consumes") != null) { // path is alias for value
                        consumes = annotation.getMemberValue("consumes").toString();
                    }
                }
            }
        }

        for (CtMethod ctMethod : ctClass.getMethods()) {
            RestEndpoint restEndpoint = analyseMethod(ctMethod);
            if (restEndpoint != null) {
                // append class level path
                if (path != null) {
                    if (restEndpoint.getPath() == null) {
                        restEndpoint.setPath(path);
                    } else {
                        restEndpoint.setPath(Helper.mergePaths(path, restEndpoint.getPath()));
                    }
                }

                // use class level properties if not specified in method level
                if (restEndpoint.getHttpMethod() == null) {
                    restEndpoint.setHttpMethod(method);
                }
                if (restEndpoint.getProduceType() == null) {
                    restEndpoint.setProduceType(produces);
                }
                if (restEndpoint.getConsumeType() == null) {
                    restEndpoint.setConsumeType(consumes);
                }

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

    private boolean isController(CtClass ctClass) {
        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.getTypeName().equals("org.springframework.stereotype.Controller") ||
                        annotation.getTypeName().equals("org.springframework.web.bind.annotation.RestController")) { // TODO: use constant
                    return true;
                }
            }
        }
        return false;
    }

    private RestEndpoint analyseMethod(CtMethod ctMethod) {
        RestEndpoint restEndpoint = new RestEndpoint();

        boolean isRestHandlerMethod = false;

        AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (annotationsAttribute != null) {
            Annotation[] annotations = annotationsAttribute.getAnnotations();
            for (Annotation annotation : annotations) {
                String annotationType = annotation.getTypeName();
                boolean isRestAnnotation = true;

                if (annotationType.equals("org.springframework.web.bind.annotation.RequestMapping")) { // TODO: use constant
                    if (annotation.getMemberValue("value") != null) {
                        restEndpoint.setPath(annotation.getMemberValue("value").toString());
                    }
                    if (annotation.getMemberValue("path") != null) { // path is alias for value
                        restEndpoint.setPath(annotation.getMemberValue("path").toString());
                    }
                    if (annotation.getMemberValue("method") != null) { // path is alias for value
                        restEndpoint.setHttpMethod(annotationToHttpMethod(annotation.getMemberValue("method").toString()));
                    }
                    if (annotation.getMemberValue("produces") != null) { // path is alias for value
                        restEndpoint.setProduceType(annotation.getMemberValue("produces").toString());
                    }
                    if (annotation.getMemberValue("consumes") != null) { // path is alias for value
                        restEndpoint.setConsumeType(annotation.getMemberValue("consumes").toString());
                    }
                } else if (annotationType.equals("org.springframework.web.bind.annotation.GetMapping")) {
                    restEndpoint.setPath(annotation.getMemberValue("value").toString());
                    restEndpoint.setHttpMethod(HttpMethod.GET);
                } else if (annotationType.equals("org.springframework.web.bind.annotation.PostMapping")) {
                    restEndpoint.setPath(annotation.getMemberValue("value").toString());
                    restEndpoint.setHttpMethod(HttpMethod.POST);
                } else {
                    isRestAnnotation = false;
                }
                // TODO: add other http methods

                // true if at least one JAX-RS annotation found
                isRestHandlerMethod = isRestHandlerMethod || isRestAnnotation;
            }
        }

        // if it is not a rest handler method, don't do further parameter analysis
        if (!isRestHandlerMethod) {
            return null;
        }

        ParameterAnnotationsAttribute parameterAnnotationsAttribute = (ParameterAnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(ParameterAnnotationsAttribute.visibleTag);
        if (parameterAnnotationsAttribute != null) {
            Annotation[][] annotationsList = parameterAnnotationsAttribute.getAnnotations();
            for (Annotation[] annotations : annotationsList) {
                String defaultValue = "";
                Param pathParam = null, queryParam = null;

                for (Annotation annotation : annotations) {
                    String annotationType = annotation.getTypeName();

                    if (annotationType.equals("org.springframework.web.bind.annotation.PathVariable")) { // TODO: use constant
                        if (annotation.getMemberValue("value") != null) {
                            pathParam = new Param(annotation.getMemberValue("value").toString());
                        } else {
                            pathParam = new Param("VARIABLE_NAME"); // TODO: get variable name
                        }

                    } else if (annotationType.equals("org.springframework.web.bind.annotation.RequestParam")) { // TODO: use constant
                        if (annotation.getMemberValue("value") != null) {
                            queryParam = new Param(annotation.getMemberValue("value").toString());
                        } else {
                            queryParam = new Param("VARIABLE_NAME"); // TODO: get variable name
                        }
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
                // TODO: formParam, headerParam, cookieParam, matrixParam
            }
        }

        return restEndpoint;
    }

    private HttpMethod annotationToHttpMethod(String annotation) {
        if (annotation.equals("org.springframework.web.bind.annotation.RequestMethod.GET")) { // TODO: use constant
            return HttpMethod.GET;
        } else if (annotation.equals("org.springframework.web.bind.annotation.RequestMethod.POST")) { // TODO: use constant
            return HttpMethod.POST;
        } else if (annotation.equals("org.springframework.web.bind.annotation.RequestMethod.PUT")) { // TODO: use constant
            return HttpMethod.PUT;
        } else if (annotation.equals("org.springframework.web.bind.annotation.RequestMethod.DELETE")) { // TODO: use constant
            return HttpMethod.DELETE;
        } else if (annotation.equals("org.springframework.web.bind.annotation.RequestMethod.OPTIONS")) { // TODO: use constant
            return HttpMethod.OPTIONS;
        } else if (annotation.equals("org.springframework.web.bind.annotation.RequestMethod.HEAD")) { // TODO: use constant
            return HttpMethod.HEAD;
        } else if (annotation.equals("org.springframework.web.bind.annotation.RequestMethod.PATCH")) { // TODO: use constant
            return HttpMethod.PATCH;
        } else {
            return null;
        }
    }
}
