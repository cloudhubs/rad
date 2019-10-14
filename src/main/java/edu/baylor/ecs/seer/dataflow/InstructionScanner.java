package edu.baylor.ecs.seer.dataflow;

import java.util.List;

public class InstructionScanner {
    public int findPosForMethodCal(List<String> instructions, String method) throws DataFlowException {
        int pos = 0;
        for (String instruction : instructions) {
            if (instruction.contains("Method " + method)) {
                return pos;
            }
            pos++;
        }
        throw new DataFlowException("method not found");
    }

    public String peekImmediateStringVariable(List<String> instructions, int pos) throws DataFlowException {
        while (pos >= 0) {
            pos--;

            String instruction = instructions.get(pos);

            if (instruction.startsWith("ldc")) {

            }
        }

        throw new DataFlowException("string variable not found");
    }
}
