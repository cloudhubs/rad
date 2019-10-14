package edu.baylor.ecs.seer.dataflow;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ASMAnalyzer {

    public void analyzeConstants(String path, String method) throws IOException, AnalyzerException {
        ClassReader cr = new ClassReader(new FileInputStream(new File(path)));
        ClassNode cn = new ClassNode(Opcodes.ASM5);
        cr.accept(cn, 0);

        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals(method)) {
                continue;
            }

            Analyzer<ConstantTracker.ConstantValue> analyzer = new Analyzer<>(new ConstantTracker());
            analyzer.analyze(cn.name, mn);

            int i = -1;
            for (Frame<ConstantTracker.ConstantValue> frame : analyzer.getFrames()) {
                i++;
                if (frame == null) {
                    continue;
                }

                AbstractInsnNode n = mn.instructions.get(i);
                if (n.getOpcode() != Opcodes.ALOAD) {
                    continue;
                }

                VarInsnNode vn = (VarInsnNode) n;
                System.out.println("accessing variable # " + vn.var);
                ConstantTracker.ConstantValue var = frame.getLocal(vn.var);
                System.out.println("\tcontains " + var.value);
            }
        }
    }
}
