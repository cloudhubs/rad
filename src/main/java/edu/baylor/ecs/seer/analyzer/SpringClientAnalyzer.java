package edu.baylor.ecs.seer.analyzer;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.springframework.stereotype.Component;

@Component
public class SpringClientAnalyzer {

    public void find(CtClass ctClass, String targetMethod, String targetClass) {
        for (CtMethod ctMethod : ctClass.getMethods()) {
            findCaller(ctMethod, targetMethod, targetClass);
        }
    }

    public void findCaller(CtMethod method, String targetMethod, String targetClass) {
        // Instrument the method to pull out the method calls
        // Then filter method calls by methodName and className
        Analyzer a = new Analyzer();
        try {
            Frame[] frames = a.analyze(method);
            if (frames != null)
                for (Frame frame : frames) System.out.println("%%%" + frame);
        } catch (BadBytecode e) {
            e.printStackTrace();
        }

        try {
            method.instrument(
                    new ExprEditor() {
                        int line = 0;

                        public void edit(MethodCall m) {
                            //System.out.println(m.getMethodName());
                            //System.out.println(m.getClassName());
                            if (m.getClassName().equals(targetClass) && m.getMethodName().equals(targetMethod)) {
                                System.out.println("=========" + method.getSignature() + " " + method.getName());
                                System.out.println("##" + m.getSignature());
                                System.out.println("##" + m.where().getMethodInfo());
                                System.out.println("#" + m.indexOfBytecode());

                                line = m.getLineNumber();


                            }
                        }

                        public void edit(Cast c) {
                            if (line > 0) {
                                System.out.println(line + " " + c.getLineNumber());
                                try {
                                    System.out.println("****" + c.getType().toString());
                                } catch (NotFoundException e) {
                                    System.err.println(e);
                                }

                            }
                        }
                    }
            );
        } catch (CannotCompileException e) {
            System.err.println(e.toString());
        }
    }
}
