package ovid.preproc;

import java.util.ArrayList;

public class StringLiteralPositionRecorder implements IStringLiteral {
    ArrayList<Integer> stringStartIndices;
    ArrayList<Integer> stringEndIndices;
    
    public StringLiteralPositionRecorder(ArrayList<Integer> stringStartIndices, ArrayList<Integer> stringEndIndices) {
        this.stringStartIndices = stringStartIndices;
        this.stringEndIndices = stringEndIndices;
    }
    
    public int locateStringAction(StringBuffer content, int startIndex, int endIndex) {
        stringStartIndices.add(startIndex);
        stringEndIndices.add(endIndex);
        return endIndex;
    }
}
