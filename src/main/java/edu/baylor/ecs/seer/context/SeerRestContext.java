package edu.baylor.ecs.seer.context;

import edu.baylor.ecs.seer.entity.RestEndpoint;

import java.util.List;

public class SeerRestContext {
    private List<RestEndpoint> restEndpoints;

    public List<RestEndpoint> getRestEndpoints() {
        return restEndpoints;
    }
}
