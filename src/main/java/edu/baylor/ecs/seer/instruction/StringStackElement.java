package edu.baylor.ecs.seer.instruction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class StringStackElement {
    public enum StringStackElementType {
        CONSTANT, PARAM, FIELD
    }

    private StringStackElementType type;
    private String value;

    // stack = LIFO = bottom-up
    public static String mergeStackElements(List<StringStackElement> stringStackElements) {
        StringBuilder value = new StringBuilder();

        for (StringStackElement stackElement : stringStackElements) {
            if (stackElement.type == StringStackElementType.CONSTANT) {
                value.insert(0, stackElement.value);
            } else if (stackElement.type == StringStackElementType.PARAM) {
                value.insert(0, "{" + stackElement.value + "}");
            } else if (stackElement.type == StringStackElementType.FIELD) {
                value.insert(0, "[" + stackElement.value + "]");
            }
        }

        return value.toString();
    }
}
