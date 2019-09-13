package edu.baylor.ecs.seer.context;

import edu.baylor.ecs.seer.entity.RestEndpoint;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class SeerRestContext {
    private List<RestEndpoint> restEndpoints = new ArrayList<>();
}
