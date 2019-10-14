package edu.baylor.ecs.seer.dataflow;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class BasicInterpreterImpl extends BasicInterpreter {
    public BasicInterpreterImpl() {
        super(458752);
    }

    @Override
    public BasicValue newValue(Type type) {
        return type != null && (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) ?
                new BasicValue(type) : super.newValue(type);
    }

    @Override
    public BasicValue merge(BasicValue a, BasicValue b) {
        if (a.equals(b)) return a;
        if (a.isReference() && b.isReference())
            // this is the place to consider the actual type hierarchy if you want
            return BasicValue.REFERENCE_VALUE;
        return BasicValue.UNINITIALIZED_VALUE;
    }
}
