package edu.baylor.ecs.seer.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Param {
    private String name;
    // private String type; // TODO: need to analyse local variables to get param type
    private String defaultValue;

    public Param(String name) {
        this.name = name;
    }
}
