package edu.baylor.ecs.seer.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RestFlow {
    private String resourcePath;
    private String className;
    private String methodName;

    private List<RestEntity> children;
}
