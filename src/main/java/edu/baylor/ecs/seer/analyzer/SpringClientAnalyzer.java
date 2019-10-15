package edu.baylor.ecs.seer.analyzer;

import edu.baylor.ecs.seer.dataflow.DataFlowException;
import edu.baylor.ecs.seer.dataflow.LocalVariableScanner;
import edu.baylor.ecs.seer.instruction.InstructionInfo;
import edu.baylor.ecs.seer.instruction.InstructionScanner;
import edu.baylor.ecs.seer.instruction.StringStackElement;
import edu.baylor.ecs.seer.model.HttpMethod;
import edu.baylor.ecs.seer.model.RestEntity;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SpringClientAnalyzer {

    @AllArgsConstructor
    private static class RestTemplateMethod {
        String restTemplateMethod;
        HttpMethod httpMethod;
        int urlParamIndex;
    }

    private static final RestTemplateMethod[] restTemplateMethods = {
            new RestTemplateMethod("getForObject", HttpMethod.GET, 2),
            new RestTemplateMethod("getForEntity", HttpMethod.GET, 2),
            new RestTemplateMethod("exchange", HttpMethod.GET, 4),
            new RestTemplateMethod("postForObject", HttpMethod.POST, 3),
            new RestTemplateMethod("delete", HttpMethod.DELETE, 1),
    };

    public static final String restTemplateClass = "org.springframework.web.client.RestTemplate";

    public List<RestEntity> getRestEntity(CtClass ctClass) {
        List<RestEntity> restEntities = new ArrayList<>();

        for (CtMethod ctMethod : ctClass.getMethods()) {

            // get instructions
            List<InstructionInfo> instructions = InstructionScanner.scan(ctMethod);

            // find caller index
            int index = 0;
            RestTemplateMethod foundMethod = null;

            for (RestTemplateMethod targetMethod : restTemplateMethods) {
                try {
                    index = LocalVariableScanner.findIndexForMethodCal(instructions, restTemplateClass + "." + targetMethod.restTemplateMethod);
                    foundMethod = targetMethod;
                    break;
                } catch (DataFlowException ignore) {
                }
            }

            // not a rest entity
            if (foundMethod == null) {
                continue;
            }

            log.info(ctMethod.getLongName());

            // find url
            try {
                List<StringStackElement> stringStackElements = LocalVariableScanner.peekParamForMethodCall(
                        instructions, index, foundMethod.urlParamIndex);

                String url = StringStackElement.mergeStackElements(stringStackElements);
                log.info(url);

                // create rest entity
                RestEntity restEntity = new RestEntity();
                restEntity.setClient(true);
                restEntity.setHttpMethod(foundMethod.httpMethod);
                restEntity.setUrl(url);

                // add class and method signatures
                restEntity.setClassName(ctClass.getName());
                restEntity.setMethodName(ctMethod.getName());
                restEntity.setReturnType(Helper.getReturnType(ctMethod));

                restEntities.add(restEntity);

            } catch (DataFlowException e) {
                log.error(e.toString());
            }
        }
        return restEntities;
    }
}
