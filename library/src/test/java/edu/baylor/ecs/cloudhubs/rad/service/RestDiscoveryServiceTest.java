package edu.baylor.ecs.cloudhubs.rad.service;

import edu.baylor.ecs.cloudhubs.rad.context.RequestContext;
import edu.baylor.ecs.cloudhubs.rad.context.ResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class RestDiscoveryServiceTest {

    @Test
    void generateResponseContext() {
        RestDiscoveryService restDiscoveryService = new RestDiscoveryService();
        RequestContext requestContext = new RequestContext(
                "../sample/target",
                "edu/baylor/ecs/cloudhubs/rad/sample",
                null
        );

        ResponseContext responseContext = restDiscoveryService.generateResponseContext(requestContext);
        assertEquals(responseContext.getRestFlowContext().getRestFlows().size(), 6);
    }
}