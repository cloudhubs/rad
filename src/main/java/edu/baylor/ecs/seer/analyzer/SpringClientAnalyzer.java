package edu.baylor.ecs.seer.analyzer;

import edu.baylor.ecs.seer.model.RestEntity;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpringClientAnalyzer {

    public List<RestEntity> getRestEntity(CtClass ctClass) {
        List<RestEntity> restEntities = new ArrayList<>();

        for (CtMethod ctMethod : ctClass.getMethods()) {
            // TODO
        }

        return restEntities;
    }
}
