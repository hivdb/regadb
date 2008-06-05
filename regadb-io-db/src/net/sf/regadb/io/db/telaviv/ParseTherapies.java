package net.sf.regadb.io.db.telaviv;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import net.sf.regadb.io.db.telaviv.ParseDrugs.Drug;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.util.frequency.Frequency;
import net.sf.regadb.util.pair.Pair;

public class ParseTherapies extends Parser{
	private UniqueObjects<Pair<Integer,Therapy>> uniques = new UniqueObjects<Pair<Integer,Therapy>>(){
		protected String getHashKey(Pair<Integer,Therapy> t){
			return t.getValue().getStartDate().getTime()+"";
		}
	};

    public ParseTherapies(Logging logger, List<DateFormat> df){
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
        int CRow = therapiesTable.findColumn("Row");
        
        
        
        for(int i=1; i<therapiesTable.numRows(); ++i){
            String id = therapiesTable.valueAt(CId, i);
            String trDate = therapiesTable.valueAt(CTrDate, i);
            String trDateStop = therapiesTable.valueAt(CTrDateStop, i);
            String treatChange = therapiesTable.valueAt(CTreatChange,i);
            String coop = therapiesTable.valueAt(CCoop, i);
            String note = therapiesTable.valueAt(CNote, i);
            String row = therapiesTable.valueAt(CRow,i);
            
            Patient p = patients.get(id);
            
            if(p != null){
                Date sd, ed;
                sd = getDate(trDate);
                ed = getDate(trDateStop);
                
                if(sd != null && sd.equals(ed)){
                	logWarn(p,"Therapy start date equals stop date",therapiesFile,i,trDate);
                }
                else if(sd != null){
	                Therapy tp = p.createTherapy(sd);

                    if(ed != null){
                        tp.setStopDate(ed);
                        
                        if(check(treatChange)){
                            TherapyMotivation tm = new TherapyMotivation(mapping.getMapping(motivationMapFile.getAbsolutePath(), treatChange));
                            tp.setTherapyMotivation(tm);
                        }
                    }
                    if(check(note))
                        tp.setComment(note);
	                    
                    addDrugs(drugs,tp,therapiesTable,i,CTrDate+1,CTrDateStop);

	                int irow = Integer.parseInt(row);
	            	Pair<Integer,Therapy> pair2 = null, pair = new Pair<Integer,Therapy>(irow,tp);
	                pair2 = uniques.exists(p, pair);
	                
	                if(pair2 != null){
	                	logWarn(p,"duplicate therapy, keeping highest row number",therapiesFile,i,"rows: "+ pair.getKey() +", "+ pair2.getKey());
	                	
	                	if(pair.getKey() > pair2.getKey()){
	                		p.getTherapies().remove(pair2.getValue());
	                		uniques.remove(p, pair2);
	                		uniques.add(p, pair);
	                	}
	                	else{
	                		p.getTherapies().remove(pair.getValue());
	                	}
	                }
                }
                else{
                    logWarn(p,"Invalid start date",therapiesFile,i,trDate);
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
