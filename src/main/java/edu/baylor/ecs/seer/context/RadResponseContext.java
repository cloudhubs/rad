package edu.baylor.ecs.seer.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class RadResponseContext {
    private RadRequestContext request;
    private List<SeerRestEntityContext> restEntityContexts = new ArrayList<>();
    private SeerRestFlowContext restFlowContext;
}
