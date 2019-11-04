package edu.baylor.ecs.cloudhubs.rad.context;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RadRequestContext {
    private String pathToCompiledMicroservices;
    private String organizationPath;
    private String outputPath;
}
