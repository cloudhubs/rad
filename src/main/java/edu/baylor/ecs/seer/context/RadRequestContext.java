package edu.baylor.ecs.seer.context;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RadRequestContext {
    private String pathToCompiledMicroservices;
    private String organizationPath;
}