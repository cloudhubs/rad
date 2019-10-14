package edu.baylor.ecs.seer.dataflow;

import edu.baylor.ecs.seer.instruction.IndexWrapper;
import edu.baylor.ecs.seer.instruction.InstructionInfo;

import java.util.List;

public class LocalVariableScanner {

    public static int findIndexForMethodCal(List<InstructionInfo> instructions, String method) throws DataFlowException {
        int index = 0;
        for (InstructionInfo instruction : instructions) {
            if (instruction.getInstruction() instanceof IndexWrapper) {
                IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

                if (indexWrapper.getType().equals("Method") && indexWrapper.getValue() instanceof String) {
                    String value = (String) indexWrapper.getValue();
                    if (value.contains(method)) {
                        return index;
                    }
                }
            }
            index++;
        }
        throw new DataFlowException("method not found");
    }

    public static String peekImmediateStringVariable(List<InstructionInfo> instructions, int index) throws DataFlowException {
        StringBuilder value = new StringBuilder();
        boolean appendStack = false;
        boolean fieldAccess = false;

        for (index = index - 1; index >= 0 && index < instructions.size(); index--) {
            InstructionInfo instruction = instructions.get(index);

            String curValue = null;

            if (getLDC(instruction) != null) {
                curValue = getLDC(instruction);
            } else if (getLoadInstructionPointer(instruction) != null) {
                int pointer = getLoadInstructionPointer(instruction);

                if (fieldAccess) { // must have aload_0 before field access
                    fieldAccess = false;
                    if (pointer != 0) {
                        throw new DataFlowException("field access error");
                    }
                } else {
                    try {
                        int storeIndex = peekImmediateStoreIndex(instructions, index, pointer);
                        curValue = peekImmediateStringVariable(instructions, storeIndex); // recursive call
                    } catch (DataFlowException e) { // not declared inside the method, possibly method parameter
                        curValue = "{" + pointer + "}"; // TODO
                    }
                }
            } else if (getFieldAccess(instruction) != null) {
                curValue = "{" + getFieldAccess(instruction) + "}";
                fieldAccess = true;
            } else if (isStringBuilderAppend(instruction)) {
                appendStack = true;
            } else if (isStringBuilderInit(instruction)) {
                if (appendStack) {
                    return value.toString();
                }
            }

            if (curValue != null) { // string constant found
                if (!appendStack) { // no append operation required
                    return curValue;
                } else { // append until StringBuilder Init found
                    value.append(curValue);
                }
            }
        }

        throw new DataFlowException("string variable not found");
    }

    private static boolean isStringBuilderAppend(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("invokevirtual") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("Method")) {
                String value = (String) indexWrapper.getValue();
                return value.contains("java.lang.StringBuilder.append");
            }
        }
        return false;
    }

    private static boolean isStringBuilderInit(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("invokespecial") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("Method")) {
                String value = (String) indexWrapper.getValue();
                return value.contains("java.lang.StringBuilder.<init>");
            }
        }
        return false;
    }

    private static String getFieldAccess(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("getfield") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("Field")) {
                String value = (String) indexWrapper.getValue();
                return value.split("\\(")[0];
            }
        }
        return null;
    }

    public static int peekImmediateStoreIndex(List<InstructionInfo> instructions, int index, int pointer) throws DataFlowException {
        for (index = index - 1; index >= 0 && index < instructions.size(); index--) {
            InstructionInfo instruction = instructions.get(index);

            if (isStoreInstructionPointer(instruction, pointer)) {
                return index;
            }
        }

        throw new DataFlowException("store pointer not found");
    }

    private static Integer getLoadInstructionPointer(InstructionInfo instruction) {
        splitOneByteLoadInstruction(instruction); // aload_1 to aload 1
        if (instruction.getOpcode().equals("aload") || instruction.getOpcode().equals("iload")) {
            return (int) instruction.getInstruction();
        }
        return null;
    }

    private static boolean isStoreInstructionPointer(InstructionInfo instruction, int pointer) {
        splitOneByteStoreInstruction(instruction); // astore_1 to astore 1
        if (instruction.getOpcode().equals("astore") || instruction.getOpcode().equals("istore")) {
            return pointer == (int) instruction.getInstruction();
        }
        return false;
    }

    private static void splitOneByteLoadInstruction(InstructionInfo instruction) {
        if (instruction.getOpcode().contains("aload_")) { // aload_1, aload_2
            String value = instruction.getOpcode().replace("aload_", "");
            instruction.setOpcode("aload");
            instruction.setInstruction(Integer.parseInt(value));
        } else if (instruction.getOpcode().contains("iload_")) { // iload_1, iload_2
            String value = instruction.getOpcode().replace("iload_", "");
            instruction.setOpcode("iload");
            instruction.setInstruction(Integer.parseInt(value));
        }
    }

    public static void splitOneByteStoreInstruction(InstructionInfo instruction) {
        if (instruction.getOpcode().contains("astore_")) { // astore_1, astore_2
            String value = instruction.getOpcode().replace("astore_", "");
            instruction.setOpcode("astore");
            instruction.setInstruction(Integer.parseInt(value));
        } else if (instruction.getOpcode().contains("istore_")) { // istore_1, istore_2
            String value = instruction.getOpcode().replace("istore_", "");
            instruction.setOpcode("istore");
            instruction.setInstruction(Integer.parseInt(value));
        }
    }

    public static String getLDC(InstructionInfo instruction) {
        if (instruction.getOpcode().equals("ldc") && instruction.getInstruction() instanceof IndexWrapper) {
            IndexWrapper indexWrapper = (IndexWrapper) instruction.getInstruction();

            if (indexWrapper.getType().equals("int")) {
                return "" + (int) indexWrapper.getValue();
            } else if (indexWrapper.getType().equals("String")) {
                return (String) indexWrapper.getValue();
            }
        }

        return null;
    }
}

