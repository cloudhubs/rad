package edu.baylor.ecs.seer.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class RestEntity {
    private boolean isClient;

    private String url;

    private String applicationName; // used in eureka discovery
    private String ribbonServerName;

    private String resourcePath;
    private String className;
    private String methodName;
    private String returnType;

    private String path;
    private List<Param> pathParams;
    private List<Param> formParams;
    private List<Param> queryParams;
    private List<Param> headerParams;
    private List<Param> cookieParams;
    private List<Param> matrixParams;
    private HttpMethod httpMethod;
    private String consumeType; // can be any mime type
    private String produceType; // can be any mime type

    public void addPathParam(Param pathParam) {
        if (pathParams == null) {
            pathParams = new ArrayList<>();
        }
        pathParams.add(pathParam);
    }

    public void addQueryParam(Param queryParam) {
        if (queryParams == null) {
            queryParams = new ArrayList<>();
        }
        queryParams.add(queryParam);
    }

    public void addFormParam(Param formParam) {
        if (formParams == null) {
            formParams = new ArrayList<>();
        }
        formParams.add(formParam);
    }
}
