package edu.baylor.ecs.seer.analyzer;

import edu.baylor.ecs.seer.dataflow.DataFlowException;
import edu.baylor.ecs.seer.dataflow.LocalVariableScanner;
import edu.baylor.ecs.seer.instruction.InstructionInfo;
import edu.baylor.ecs.seer.instruction.InstructionScanner;
import edu.baylor.ecs.seer.instruction.StringStackElement;
import edu.baylor.ecs.seer.model.HttpMethod;
import edu.baylor.ecs.seer.model.RestEntity;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
public class SpringClientAnalyzer {

    @AllArgsConstructor
    private static class RestTemplateMethod {
        String restTemplateMethod;
        HttpMethod httpMethod;
        int numberOfParams;
    }

    private static final RestTemplateMethod[] restTemplateMethods = {
            new RestTemplateMethod("getForObject", HttpMethod.GET, 2),
            new RestTemplateMethod("getForEntity", HttpMethod.GET, 2),
            new RestTemplateMethod("exchange", HttpMethod.GET, 4),
            new RestTemplateMethod("postForObject", HttpMethod.POST, 3),
            new RestTemplateMethod("delete", HttpMethod.DELETE, 1),
    };

    public static final String restTemplateClass = "org.springframework.web.client.RestTemplate";

    public List<RestEntity> getRestEntity(CtClass ctClass, Properties properties) {
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
                        instructions, index, foundMethod.numberOfParams);

                // find field values defined by @value annotation
                for (StringStackElement stringStackElement : stringStackElements) {
                    if (stringStackElement.getType() == StringStackElement.StringStackElementType.FIELD) {
                        String simpleFieldName = stringStackElement.getValue().replace(ctClass.getName() + ".", "");

                        CtField ctField = ctClass.getField(simpleFieldName);

                        String propertyName = Helper.getFieldAnnotationValue(ctField);
                        String propertyValue = null;

                        if (propertyName != null && properties != null) {
                            log.info(propertyName);
                            propertyValue = properties.getProperty(propertyName);
                            propertyValue = Helper.removeEnclosedQuotations(propertyValue);
                            propertyValue = Helper.removeEnclosedSingleQuotations(propertyValue);
                        }

                        if (propertyValue != null) {
                            stringStackElement.setType(StringStackElement.StringStackElementType.CONSTANT);
                            stringStackElement.setValue(propertyValue);
                        } else {
                            throw new DataFlowException("Can not resolve field value for " + stringStackElement.getValue());
                        }
                    }
                }

                // build the url from stack elements
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

            } catch (DataFlowException | NotFoundException e) {
                log.error(e.toString());
            }
        }
        return restEntities;
    }
}
