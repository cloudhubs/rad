package edu.baylor.ecs.cloudhubs.rad.service;

import edu.baylor.ecs.cloudhubs.rad.analyzer.Helper;
import edu.baylor.ecs.cloudhubs.rad.context.SeerRestEntityContext;
import edu.baylor.ecs.cloudhubs.rad.context.SeerRestFlowContext;
import edu.baylor.ecs.cloudhubs.rad.model.RestEntity;
import edu.baylor.ecs.cloudhubs.rad.model.RestFlow;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestFlowService {
    public SeerRestFlowContext getRestFlowContext(List<SeerRestEntityContext> restEntityContexts) {
        List<RestEntity> serverEntities = new ArrayList<>();
        List<RestEntity> clientEntities = new ArrayList<>();

        for (SeerRestEntityContext restEntityContext : restEntityContexts) {
            for (RestEntity restEntity : restEntityContext.getRestEntities()) {
                if (restEntity.isClient()) clientEntities.add(restEntity);
                else serverEntities.add(restEntity);
            }
        }

        SeerRestFlowContext restFlowContext = new SeerRestFlowContext();
        restFlowContext.getRestFlows().addAll(getRestFlows(serverEntities, clientEntities));
        // restFlowContext.getRestFlows().addAll(getPossibleRestFlows(serverEntities, clientEntities));

        return restFlowContext;
    }

    private List<RestFlow> getRestFlows(List<RestEntity> serverEntities, List<RestEntity> clientEntities) {
        List<RestFlow> restFlows = new ArrayList<>();

        // populate RestFlow
        for (RestEntity restClientEntity : clientEntities) {
            for (RestEntity restServerEntity : serverEntities) {
                // match url and http method
                if (restClientEntity.getHttpMethod() == restServerEntity.getHttpMethod() &&
                        Helper.matchUrl(restClientEntity.getUrl(), restServerEntity.getUrl())) {

                    createRestFlow(restFlows, restServerEntity, restClientEntity);
                }
            }
        }
        return restFlows;
    }

    private List<RestFlow> getPossibleRestFlows(List<RestEntity> serverEntities, List<RestEntity> clientEntities) {
        List<RestFlow> restFlows = new ArrayList<>();

        // populate RestFlow
        for (RestEntity restClientEntity : clientEntities) {
            for (RestEntity restServerEntity : serverEntities) {
                // match return type and http method
                if (restClientEntity.getHttpMethod() == restServerEntity.getHttpMethod() &&
                        restClientEntity.getReturnType() != null &&
                        restServerEntity.getReturnType() != null &&
                        restClientEntity.getReturnType().equals(restServerEntity.getReturnType())) {

                    // narrow down, match server name if specified
                    String serverName = restClientEntity.getRibbonServerName();
                    String applicationName = restServerEntity.getApplicationName();
                    if (serverName != null && !serverName.equals(applicationName))
                        continue;

                    createRestFlow(restFlows, restServerEntity, restClientEntity);
                }
            }
        }

        return restFlows;
    }

    private void createRestFlow(List<RestFlow> restFlows, RestEntity server, RestEntity client) {
        // search if there is already an entry for this client
        for (RestFlow restFlow : restFlows) {
            if (restFlow.getResourcePath().equals(client.getResourcePath()) &&
                    restFlow.getClassName().equals(client.getClassName()) &&
                    restFlow.getMethodName().equals(client.getMethodName())) {

                restFlow.getServers().add(server);
                return;
            }
        }

        RestFlow restFlow = new RestFlow();

        restFlow.setResourcePath(client.getResourcePath());
        restFlow.setClassName(client.getClassName());
        restFlow.setMethodName(client.getMethodName());

        restFlow.setServers(new ArrayList<>());
        restFlow.getServers().add(server);

        restFlows.add(restFlow);
    }
}
