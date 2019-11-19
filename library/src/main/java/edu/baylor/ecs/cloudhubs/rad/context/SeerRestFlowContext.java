package edu.baylor.ecs.cloudhubs.rad.context;

import edu.baylor.ecs.cloudhubs.rad.model.RestFlow;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a list of {@link edu.baylor.ecs.cloudhubs.rad.model.RestFlow}
 * resulted after performing the REST flow analysis for all microservices.
 *
 * @author Dipta Das
 */

@Getter
@ToString
public class SeerRestFlowContext {
    private List<RestFlow> restFlows = new ArrayList<>();
}
