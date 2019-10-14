package edu.baylor.ecs.seer.dataflow;

import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;

import java.util.List;

public class LocalVariableScanner {

    public String peekImmediateStringVariable(CtMethod ctMethod, List<String> instructions, int currentPos) throws DataFlowException {
        currentPos--;
        if (currentPos < 0) throw new DataFlowException("position out of bound");

        try {
            return getLDC(ctMethod, currentPos);
        } catch (DataFlowException ignored) {
        }

        throw new DataFlowException("cannot peek");
    }

    private String getLDC(CtMethod ctMethod, int pos) throws DataFlowException {
        MethodInfo info = ctMethod.getMethodInfo2();
        ConstPool pool = info.getConstPool();

        int tag = pool.getTag(pos);
        if (tag == 8) {
            return pool.getStringInfo(pos);
        } else {
            throw new DataFlowException("not string LDC");
        }
    }
}


