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
            getExpectedRestEntity("doGetMapping", HttpMethod.GET, "SampleModel", false),
            getExpectedRestEntity("doRequestMappingGet", HttpMethod.GET, "SampleModel", false),
            getExpectedRestEntity("doPostMapping", HttpMethod.POST, "SampleModel", false),
            getExpectedRestEntity("doRequestMappingPost", HttpMethod.POST, "SampleModel", false),
            getExpectedRestEntity("doDeleteMapping", HttpMethod.DELETE, "void", false),
            getExpectedRestEntity("doRequestMappingDelete", HttpMethod.DELETE, "void", false)
    );

    private List<RestEntity> expectedRestCalls = Arrays.asList(
            getExpectedRestEntity("doGetForObject", HttpMethod.GET, "SampleModel", true),
            getExpectedRestEntity("doGetForEntity", HttpMethod.GET, "SampleModel", true),
            getExpectedRestEntity("doExchangeGet", HttpMethod.GET, "SampleModel", true),
            getExpectedRestEntity("doPostForObject", HttpMethod.POST, "SampleModel", true),
            getExpectedRestEntity("doPostForEntity", HttpMethod.POST, "SampleModel", true),
            getExpectedRestEntity("doExchangePost", HttpMethod.POST, "SampleModel", true),
            getExpectedRestEntity("doDelete", HttpMethod.DELETE, "void", true),
            getExpectedRestEntity("doDeleteRequestMapping", HttpMethod.DELETE, "void", true)
    );

    private RestEntity getExpectedRestEntity(String methodName, HttpMethod httpMethod, String returnType, boolean isClient) {
        RestEntity restEntity = new RestEntity();
        restEntity.setMethodName(methodName);
        restEntity.setHttpMethod(httpMethod);
        restEntity.setReturnType(returnType);
        restEntity.setClient(isClient);
        return restEntity;
    }

//    @Test
//    void generateResponseContext() {
//        RestDiscoveryService restDiscoveryService = new RestDiscoveryService();
//        RequestContext requestContext = new RequestContext(
//                "../sample/target",
//                "edu/baylor/ecs/cloudhubs/rad/sample",
//                null
//        );
//
//        ResponseContext responseContext = restDiscoveryService.generateResponseContext(requestContext);
//        assertEquals(responseContext.getRestEntityContexts().size(), 1);
//        assertEquals(responseContext.getRestEntityContexts().get(0).getRestEntities().size(), 14); // client + server
//        assertEquals(responseContext.getRestFlowContext().getRestFlows().size(), 8);
//
//        int countEndpoints = 0;
//
//        for (RestEntity restEntity : responseContext.getRestEntityContexts().get(0).getRestEntities()) {
//            for (RestEntity expectedRestEndpoint : expectedRestEndpoints) {
//                if (restEntity.getMethodName().equals(expectedRestEndpoint.getMethodName())) {
//                    assertEquals(restEntity.getHttpMethod(), expectedRestEndpoint.getHttpMethod());
//                    assertTrue(restEntity.getReturnType().contains(expectedRestEndpoint.getReturnType()));
//                    assertEquals(restEntity.isClient(), expectedRestEndpoint.isClient());
//                    countEndpoints++;
//                }
//            }
//        }
//
//        assertEquals(countEndpoints, 6);
//
//        int countRestCalls = 0;
//
//        for (RestEntity restEntity : responseContext.getRestEntityContexts().get(0).getRestEntities()) {
//            for (RestEntity expectedRestCalls : expectedRestCalls) {
//                if (restEntity.getMethodName().equals(expectedRestCalls.getMethodName())) {
//                    assertEquals(restEntity.getHttpMethod(), expectedRestCalls.getHttpMethod());
//                    assertTrue(restEntity.getReturnType().contains(expectedRestCalls.getReturnType()));
//                    assertEquals(restEntity.isClient(), expectedRestCalls.isClient());
//                    countRestCalls++;
//                }
//            }
//        }
//
//        assertEquals(countRestCalls, 8);
//    }
}