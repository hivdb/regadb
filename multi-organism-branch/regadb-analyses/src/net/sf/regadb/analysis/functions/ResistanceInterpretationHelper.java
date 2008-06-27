package net.sf.regadb.analysis.functions;

public class ResistanceInterpretationHelper {
    public static String getSIRRepresentation(Double gss) {
        if(gss==null) {
            return "NA";
        }
        else if(gss == 0.0) {
                return "R";
        }
        else if(gss == 0.5 || gss == 0.75) {
                return "I";
        }
        else if(gss == 1.0 || gss == 1.5) {
                return "S";
        }
        else {
            return "Cannot interprete";
        }
    }
}
