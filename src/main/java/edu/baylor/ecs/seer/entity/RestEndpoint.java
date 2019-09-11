package edu.baylor.ecs.seer.entity;

import java.util.List;

public class RestEndpoint {
    private String path;
    private List<Param> pathParams;
    private List<Param> formParams;
    private List<Param> queryParams;
    private List<Param> headerParams;
    private HttpMethod httpMethod;
    private ConsumeType consumeType;
    private ProduceType produceType;
}
