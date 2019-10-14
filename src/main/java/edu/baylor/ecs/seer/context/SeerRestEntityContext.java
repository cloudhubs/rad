package edu.baylor.ecs.seer.context;

import edu.baylor.ecs.seer.model.RestEntity;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class SeerRestEntityContext {
    private List<RestEntity> restEntities = new ArrayList<>();
}
