package edu.baylor.ecs.seer.instruction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class IndexWrapper {
    private int index;
    private String type;
    private Object value;
}
