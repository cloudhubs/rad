package edu.baylor.ecs.cloudhubs.rad.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the result after performing REST API discovery.
 * It wraps the {@link edu.baylor.ecs.cloudhubs.rad.context.RadRequestContext},
 * a list of {@link edu.baylor.ecs.cloudhubs.rad.context.SeerRestEntityContext},
 * and the {@link edu.baylor.ecs.cloudhubs.rad.context.SeerRestFlowContext}.
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
public class RadResponseContext {
    private RadRequestContext request;
    private List<SeerRestEntityContext> restEntityContexts = new ArrayList<>();
    private SeerRestFlowContext restFlowContext;
}
