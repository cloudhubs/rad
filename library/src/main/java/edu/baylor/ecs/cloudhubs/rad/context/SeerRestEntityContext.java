package edu.baylor.ecs.cloudhubs.rad.context;

import edu.baylor.ecs.cloudhubs.rad.model.RestEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SeerRestEntityContext {
    private String resourcePath;
    private List<RestEntity> restEntities = new ArrayList<>();
}
