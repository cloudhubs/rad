package edu.baylor.ecs.seer.analyzer;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class SpringClientAnalyzer {
    public String targetMethod, targetClass;
    public CtClass ctClass;

    public SpringClientAnalyzer(String targetMethod, String targetClass, CtClass ctClass) {
        this.targetMethod = targetMethod;
        this.targetClass = targetClass;
        this.ctClass = ctClass;
    }

    public void find() {
        for (CtMethod ctMethod : ctClass.getMethods()) {
            findCaller(ctMethod);
        }
    }

    public void findCaller(CtMethod method) {
        // Instrument the method to pull out the method calls
        // Then filter method calls by methodName and className
        try {
            method.instrument(
                    new ExprEditor() {
                        int line = 0;

                        public void edit(MethodCall m) {
                            if (m.getClassName().equals(targetClass) && m.getMethodName().equals(targetMethod)) {
                                System.out.println("=========" + method.getSignature() + " " + method.getName());
                                System.out.println("##" + m.getSignature());
                                System.out.println("##" + m.where().getMethodInfo());

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
