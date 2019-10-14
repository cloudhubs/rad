package edu.baylor.ecs.seer.dataflow;

import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaAssistAnalyzer {

    public void printInstruction(CtMethod ctMethod) {
        System.out.println("INSTRUCTIONS");
        InstructionPrinter.print(ctMethod, System.out);
    }

    public List<String> getInstructions(CtMethod ctMethod) {
        List<String> instructions = new ArrayList<>();

        MethodInfo info = ctMethod.getMethodInfo2();
        ConstPool pool = info.getConstPool();
        CodeAttribute code = info.getCodeAttribute();
        if (code != null) {
            int pos;
            for (CodeIterator iterator = code.iterator(); iterator.hasNext(); instructions.add(InstructionPrinter.instructionString(iterator, pos, pool))) {
                try {
                    pos = iterator.next();
                } catch (BadBytecode var8) {
                    throw new RuntimeException(var8);
                }
            }
        }

        return instructions;
    }

    public void analyzeFrame(CtMethod ctMethod) throws BadBytecode {
        Analyzer a = new Analyzer();
        Frame[] frames = a.analyze(ctMethod);

        System.out.println("FRAMES");

        int i = 0;
        for (Frame frame : frames) {
            System.out.println(i++ + " " + frame);
        }

        System.out.println(frames[6].peek());
    }

    public int getByteCodeIndex(CtMethod method, String targetMethod, String targetClass) {
        final int[] byteCodeIndex = {-1};
        try {
            method.instrument(
                    new ExprEditor() {
                        public void edit(MethodCall m) {
                            if (m.getClassName().equals(targetClass) && m.getMethodName().equals(targetMethod)) {
//                                System.out.println("#1" + method.getSignature() + " " + method.getName());
//                                System.out.println("#2" + m.getSignature());
//                                System.out.println("#3" + m.where().getMethodInfo());
//                                System.out.println("#4" + m.indexOfBytecode());
//                                System.out.println("#5" + m.getLineNumber());
                                byteCodeIndex[0] = m.indexOfBytecode();
                            }
                        }
                    }
            );
        } catch (CannotCompileException e) {
            System.err.println(e.toString());
        }

        return byteCodeIndex[0];
    }

    public CtMethod getCtMethodFromClassFile(String path, String method) throws IOException, NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new FileInputStream(path));
        return ctClass.getDeclaredMethod(method);
    }
}