package edu.baylor.ecs.cloudhubs.rad.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RadRequestContext {
    private String pathToCompiledMicroservices;
    private String organizationPath;
    private String outputPath;
}
