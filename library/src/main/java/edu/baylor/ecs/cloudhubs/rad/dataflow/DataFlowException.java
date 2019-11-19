package edu.baylor.ecs.cloudhubs.rad.dataflow;

/**
 * This class defines an exception that might occur during the DataFlow analysis.
 *
 * @author Dipta Das
 */

public class DataFlowException extends Exception {
    public DataFlowException(String reason) {
        super(reason);
    }
}
