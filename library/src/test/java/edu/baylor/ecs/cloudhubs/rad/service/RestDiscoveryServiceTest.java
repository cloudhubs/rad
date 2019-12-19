package edu.baylor.ecs.cloudhubs.rad.service;

import edu.baylor.ecs.cloudhubs.rad.context.RequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.ResponseContext;
import edu.baylor.ecs.cloudhubs.rad.model.HttpMethod;
import edu.baylor.ecs.cloudhubs.rad.model.RestEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class RestDiscoveryServiceTest {

    private List<RestEntity> expectedRestEndpoints = Arrays.asList(
            getExpectedRestEndpoint("doGetMapping", HttpMethod.GET, "SampleModel"),
            getExpectedRestEndpoint("doRequestMappingGet", HttpMethod.GET, "SampleModel"),
            getExpectedRestEndpoint("doPostMapping", HttpMethod.POST, "SampleModel"),
            getExpectedRestEndpoint("doRequestMappingPost", HttpMethod.POST, "SampleModel")
    );

    private RestEntity getExpectedRestEndpoint(String methodName, HttpMethod httpMethod, String returnType) {
        RestEntity restEntity = new RestEntity();
        restEntity.setMethodName(methodName);
        restEntity.setHttpMethod(httpMethod);
        restEntity.setReturnType(returnType);
        restEntity.setClient(false);
        return restEntity;
    }

    @Test
    void generateResponseContext() {
        RestDiscoveryService restDiscoveryService = new RestDiscoveryService();
        RequestContext requestContext = new RequestContext(
                "../sample/target",
                "edu/baylor/ecs/cloudhubs/rad/sample",
                null
        );

        ResponseContext responseContext = restDiscoveryService.generateResponseContext(requestContext);
        assertEquals(responseContext.getRestEntityContexts().size(), 1);
        assertEquals(responseContext.getRestEntityContexts().get(0).getRestEntities().size(), 10); // client + server
        assertEquals(responseContext.getRestFlowContext().getRestFlows().size(), 6);

        int countEndpoints = 0;

        for (RestEntity restEntity : responseContext.getRestEntityContexts().get(0).getRestEntities()) {
            for (RestEntity expectedRestEndpoint : expectedRestEndpoints) {
                if (restEntity.getMethodName().equals(expectedRestEndpoint.getMethodName())) {
                    assertEquals(restEntity.getHttpMethod(), expectedRestEndpoint.getHttpMethod());
                    assertTrue(restEntity.getReturnType().contains(expectedRestEndpoint.getReturnType()));
                    assertEquals(restEntity.isClient(), expectedRestEndpoint.isClient());
                    countEndpoints++;
                }
            }
        }

        assertEquals(countEndpoints, 4);
    }
}