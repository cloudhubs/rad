package edu.baylor.ecs.seer.dataflow;

import javassist.CtMethod;

import java.util.List;

/**
 * DATA FLOW EXPERIMENTS
 */
public class Main {
    private static ASMAnalyzer asmAnalyzer = new ASMAnalyzer();
    private static JavaAssistAnalyzer javaAssistAnalyzer = new JavaAssistAnalyzer();
    private static JavaParserAnalyzer javaParserAnalyzer = new JavaParserAnalyzer();
    private static LocalVariableScanner localVariableScanner = new LocalVariableScanner();

    public static void main(String[] args) throws Exception {
        System.out.println("start");

//        String compiledClasspath = "C:\\seer-lab\\cil-tms\\tms-cms\\target\\classes\\edu\\baylor\\ecs\\cms\\service\\EmsService.class";
//        String method = "getQuestionsForExam";

        String compiledClasspath = "C:\\seer-lab\\cil-rad\\target\\classes\\edu\\baylor\\ecs\\seer\\dataflow\\SampleRestClient.class";
        String method = "restCall01";

//        asmAnalyzer.analyzeConstants(compiledClasspath, method);

        CtMethod ctMethod = javaAssistAnalyzer.getCtMethodFromClassFile(compiledClasspath, method);
        javaAssistAnalyzer.printInstruction(ctMethod);

//        ctMethod = javaAssistAnalyzer.getCtMethodFromClassFile(compiledClasspath, "restCall02");
//        javaAssistAnalyzer.printInstruction(ctMethod);
//
//        ctMethod = javaAssistAnalyzer.getCtMethodFromClassFile(compiledClasspath, "restCall03");
//        javaAssistAnalyzer.printInstruction(ctMethod);

        List<String> instructions = javaAssistAnalyzer.getInstructions(ctMethod);

        String value = localVariableScanner.peekImmediateStringVariable(ctMethod, instructions, 11);
        System.out.println(value);
    }
}
