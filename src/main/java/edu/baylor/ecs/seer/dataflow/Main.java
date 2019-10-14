package edu.baylor.ecs.seer.dataflow;

import edu.baylor.ecs.seer.instruction.InstructionInfo;
import edu.baylor.ecs.seer.instruction.InstructionScanner;
import edu.baylor.ecs.seer.instruction.StringStackElement;
import javassist.CtMethod;

import java.util.List;

/**
 * DATA FLOW EXPERIMENTS
 */
public class Main {
    private static JavaAssistAnalyzer javaAssistAnalyzer = new JavaAssistAnalyzer();
//    private static ASMAnalyzer asmAnalyzer = new ASMAnalyzer();
//    private static JavaParserAnalyzer javaParserAnalyzer = new JavaParserAnalyzer();
//    private static LocalVariableScanner localVariableScanner = new LocalVariableScanner();

    public static void main(String[] args) throws Exception {
        System.out.println("start");

        String compiledClasspath = "C:\\seer-lab\\cil-tms\\tms-cms\\target\\classes\\edu\\baylor\\ecs\\cms\\service\\EmsService.class";
        String method = "getQuestionsForExam";

//        String compiledClasspath = "C:\\seer-lab\\cil-rad\\target\\classes\\edu\\baylor\\ecs\\seer\\dataflow\\SampleRestClient.class";
//        String method = "restCall06";

        CtMethod ctMethod = javaAssistAnalyzer.getCtMethodFromClassFile(compiledClasspath, method);
        // javaAssistAnalyzer.printInstruction(ctMethod);

        List<InstructionInfo> instructions = InstructionScanner.scan(ctMethod);

        int index = LocalVariableScanner.findIndexForMethodCal(instructions, "org.springframework.web.client.RestTemplate.getForObject");

        List<StringStackElement> stringStackElements = LocalVariableScanner.peekImmediateStringVariable(instructions, index);
        stringStackElements.stream().forEach(System.out::println);

        System.out.println("URL: " + StringStackElement.mergeStackElements(stringStackElements));
    }
}
