package edu.baylor.ecs.seer.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Param {
    private String name;
    private String defaultValue;

    public Param(String name) {
        this.name = name;
    }
}
