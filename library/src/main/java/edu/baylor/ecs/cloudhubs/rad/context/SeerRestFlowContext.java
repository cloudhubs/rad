package edu.baylor.ecs.cloudhubs.rad.context;

import edu.baylor.ecs.cloudhubs.rad.model.RestFlow;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class SeerRestFlowContext {
    private List<RestFlow> restFlows = new ArrayList<>();
    private List<RestFlow> possibleRestFlows = new ArrayList<>();
}
