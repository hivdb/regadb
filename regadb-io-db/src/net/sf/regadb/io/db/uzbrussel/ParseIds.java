package net.sf.regadb.io.db.uzbrussel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import net.sf.regadb.csv.Table;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;

public class ParseIds {
    private String baseDir_;
    private Map<Integer, List<String>> consultCodeHistory_;
    private Map<Integer, String> codepat_;
    
    private int counter = 0;
    
    public static void main(String [] args) {
        Map<Integer, List<String>> consultCodeHistory = new HashMap<Integer, List<String>>();
        Map<Integer, String> codepat = new HashMap<Integer, String>();
        ParseIds parseIds = new ParseIds("/home/plibin0/import/jette/import/cd/080320/", consultCodeHistory,codepat);
        parseIds.exec();
    }
    
    public ParseIds(String baseDir, Map<Integer, List<String>> consultCodeHistory, Map<Integer, String> codepat) {
        baseDir_ = baseDir;
        consultCodeHistory_ = consultCodeHistory;
        codepat_ = codepat;
    }
    
    public void exec() {
        File patCodesAndIdHistory = new File(baseDir_+"emd" + File.separatorChar + "patcodes.csv");
        try {
            BufferedReader in = new BufferedReader(new FileReader(patCodesAndIdHistory));
            String line;
            while ((line = in.readLine()) != null) {
                parseCodePatLine(line);
            }
            in.close();
        } catch (IOException e) {
            ConsoleLogger.getInstance().logError("Problem loading file: "  + patCodesAndIdHistory.getAbsolutePath());
        }
        
        File patientIdHistory = new File(baseDir_+"emd" + File.separatorChar + "pathistory.csv");
        Table patientIdHistoryTable = Utils.readTable(patientIdHistory.getAbsolutePath(), ';');
        parsePatHistoryTable(patientIdHistoryTable);
        
        checkIds();
    }
    
    private void parseCodePatLine(String line) {
        StringTokenizer st = new StringTokenizer(line, ";");
        String token;
        String patCode = null;
        List<String> codesHistory = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            token = st.nextToken().trim();
            if(patCode==null) {
                patCode = token;
            } else if(!token.equals("")){
                codesHistory.add(token);
            }
        }
        
        int id = getUniqueId(codesHistory, patCode);
        consultCodeHistory_.put(id, codesHistory);
        codepat_.put(id, patCode);
    }
    
    private void parsePatHistoryTable(Table patHistory) {
        String oldValue;
        String newValue;
        for(int i = 1; i<patHistory.numRows(); i++) {
            oldValue = patHistory.valueAt(0, i).trim();
            newValue = patHistory.valueAt(1, i).trim();
            
            for(Entry<Integer, List<String>> history : consultCodeHistory_.entrySet()) {
                if(history.getValue().contains(oldValue)) {
                    if(!history.getValue().contains(newValue)) {
                        history.getValue().add(newValue);
                    }
                    break;
                }
            }
        }
    }
    
    private int getUniqueId(List<String> codesHistory, String codePat) {
        return ++counter;
    }
    
    private void checkIds() {
        for(Entry<Integer, List<String>> history_a : consultCodeHistory_.entrySet()) {
            for(String h_a : history_a.getValue()) {
                for(Entry<Integer, List<String>> history_b : consultCodeHistory_.entrySet()) {
                    if(!history_a.getKey().equals(history_b.getKey())) {
                        for(String h_b : history_b.getValue()) {
                            if(h_a.equals(h_b)) {
                                ConsoleLogger.getInstance().logError("Duplicate consult id: "+codepat_.get(history_a.getKey()) + " -> "+ h_b);
                            }
                        }
                    }
                }
            }
        }
        
        int counter = 0;
        for(Entry<Integer, String> e1 : codepat_.entrySet()) {
            String patcode = e1.getValue();
            for(Entry<Integer, String> e2 : codepat_.entrySet()) {
                if(e2.getValue().equals(patcode)) {
                    counter++;
                }
            }
            if(counter>1) {
                ConsoleLogger.getInstance().logError("Duplicate patcode: " + patcode);
            }
            counter = 0;
        }
    }
    
    public Integer getPatientId(String consultId) {
        for(Entry<Integer, List<String>> history : consultCodeHistory_.entrySet()) {
            for(String h : history.getValue()) {
                if(h.equals(consultId)) {
                    return history.getKey();
                }
            }
        }
        return null;
    }
    
    public Integer getPatientIdForPatcode(String patCode) {
        for(Entry<Integer, String> e : codepat_.entrySet()) {
            if(e.getValue().equals(patCode)) {
                return e.getKey();
            }
        }
        return null;
    }
}
