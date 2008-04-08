package net.sf.regadb.io.db.jerusalem;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.io.db.jerusalem.ParseDrugs.Drug;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.util.frequency.Frequency;

public class ParseTherapies extends Parser{

    public ParseTherapies(Logging logger, DateFormat df){
        super(logger,df);
        setName("Therapies");
    }
    
    public void run(Map<String,Patient> patients, Map<String,Drug> drugs, File therapiesFile, File motivationMapFile){
        logInfo("Parsing therapies...");
        
        if(   !check(therapiesFile)
           || !check(motivationMapFile))
            return;
        
        Mappings mapping = Mappings.getInstance("");
        
        setCurrentFile(therapiesFile);
        Table therapiesTable = Utils.readTable(therapiesFile.getAbsolutePath());
        
        int CId = therapiesTable.findColumn("ID");
        int CTrDate = therapiesTable.findColumn("TrDate");
        int CTrDateStop = therapiesTable.findColumn("TrDateStop");
        int CTreatChange = therapiesTable.findColumn("TreatChange1");
        int CCoop = therapiesTable.findColumn("Coop1");
        int CNote = therapiesTable.findColumn("Note");
        
        
        
        for(int i=1; i<therapiesTable.numRows(); ++i){
            String id = therapiesTable.valueAt(CId, i);
            String trDate = therapiesTable.valueAt(CTrDate, i);
            String trDateStop = therapiesTable.valueAt(CTrDateStop, i);
            String treatChange = therapiesTable.valueAt(CTreatChange,i);
            String coop = therapiesTable.valueAt(CCoop, i);
            String note = therapiesTable.valueAt(CNote, i);
            
            Patient p = patients.get(id);
            
            if(p != null){
                Date d;
                d = getDate(trDate);
                
                if(d != null){
                    Therapy tp = p.createTherapy(d);
                    
                    if(check(trDateStop)){
                        d = getDate(trDateStop);
                        if(d != null){
                            tp.setStopDate(d);
                            
                            if(check(treatChange)){
                                TherapyMotivation tm = new TherapyMotivation(mapping.getMapping(motivationMapFile.getAbsolutePath(), treatChange));
                                tp.setTherapyMotivation(tm);
                            }
                        }
                        else
                            logWarn("Invalid stop date",therapiesFile,i,trDateStop);
                    }
                    
                    tp.setComment(note);
                    
                    addDrugs(drugs,tp,therapiesTable,i,CTrDate+1,CTrDateStop);
                }
                else{
                    logWarn("Invalid start date",therapiesFile,i,trDate);
                }
            }
            else{
                logWarn("Invalid patient ID",therapiesFile,i,id);
            }
        }
    }

    private void addDrugs(Map<String,Drug> drugs, Therapy tp, Table t, int row, int bIndex, int eIndex){
        Set<String> drugNos = new HashSet<String>();

        for(int j=bIndex; j<eIndex; ++j){
            String drugNo = t.valueAt(j, row);
            
            if(check(drugNo)){
                if(!drugNos.contains(drugNo)){
                    drugNos.add(drugNo);
    
                    Drug d = drugs.get(drugNo);
    
                    if(d != null && d.getType() != ParseDrugs.DrugType.UNKNOWN){
                        if(d.getType() == ParseDrugs.DrugType.COMMERCIAL){
                            TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(tp,(DrugCommercial)d.getDrug()),1.0,false,false,(long)Frequency.getDefaultFrequency());
                            tp.getTherapyCommercials().add(tc);
                        }
                        else if(d.getType() == ParseDrugs.DrugType.GENERIC){
                            TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(tp,(DrugGeneric)d.getDrug()),1.0,false,false,(long)Frequency.getDefaultFrequency());
                            tp.getTherapyGenerics().add(tg);
                        }
                    }
                }
                else{
                    logWarn("duplicate agentNo found", getCurrentFile(), row, drugNo);
                }
            }
        }
    }
}
