package net.sf.regadb.io.db.euresist;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.util.frequency.Frequency;

public class HandleTherapies {
	private ExportDB exportDb_;
	
	private List<DrugGeneric> regaDrugGenerics = Utils.prepareRegaDrugGenerics();
	private Mappings mappings_;
	
	public HandleTherapies(ExportDB exportDb) {
		exportDb_ = exportDb;
		mappings_ = Mappings.getInstance(exportDb_.getMappingPath()+File.separatorChar);
	}
	
	public void run(Map<String,Patient> patients) {
        try {
        	Map<Integer, DrugGeneric> drugMapping = new HashMap<Integer, DrugGeneric>();
        	int rtvb = getGenericDrugs(drugMapping);
        	
        	ResultSet rs = exportDb_.getDb().executeQuery("SELECT * FROM Therapies");
        	while(rs.next()) {
        		int therapyID = rs.getInt("therapyID");
        		int patientID = rs.getInt("patientID");
        		Date start = rs.getDate("start_date");
        		Date end = rs.getDate("stop_date");
        		int stopCause = rs.getInt("stop_causeID");
        		
        		Patient p = patients.get(patientID+"");
        		if(p!=null) {
        			ResultSet rs_compounds = exportDb_.getDb().executeQuery("SELECT * FROM TherapyCompounds WHERE therapyID="+therapyID);
        			List<DrugGeneric> medicineList = new ArrayList<DrugGeneric>();
        			boolean rtvb_b = false;
        			while(rs_compounds.next()) {
        				int compoundID = rs_compounds.getInt("compoundID");
        				if(compoundID==rtvb) {
        					rtvb_b = true;
        				} else {
        					DrugGeneric dg = drugMapping.get(compoundID);
        					if(dg!=null) {
        						medicineList.add(dg);
        					}
        				}
        			}
        			if(medicineList.size()>0)
        				storeTherapy(p, start, end, medicineList, rtvb_b, getTherapyMotivation(therapyID, stopCause));
        		} else {
                    ConsoleLogger.getInstance().logWarning(
                            "No patient with id " + patientID + " for therapy with id " + therapyID);
        		}
        	}
        	
        } catch(SQLException e) {
        	e.printStackTrace();
        }
	}
	
	private String getTherapyMotivation(int therapyID, int stopID) throws SQLException {
		if(stopID==0) {
			ResultSet rs_compounds = exportDb_.getDb().executeQuery("SELECT * FROM TherapyCompounds WHERE therapyID="+therapyID);
			Set<Integer> causes = new HashSet<Integer>();
			
			while(rs_compounds.next()) {
				int compoundStopCause = rs_compounds.getInt("stop_causeID");
				if(compoundStopCause!=0)
					causes.add(compoundStopCause);
			}
			
			if(causes.size()>1) {
				int min = Integer.MAX_VALUE;
				for(Integer c : causes) {
					min = Math.min(c, min);
				}
				return mappings_.getMapping("motivation.mapping", min+"");
			} else {
				return null;
			}
		} else {
			return mappings_.getMapping("motivation.mapping", stopID+"");
		}
	}
	
	private void storeTherapy(Patient p, Date start, Date end, List<DrugGeneric> medicinsList, boolean rtvb, String motivation) {
    	Therapy t = p.createTherapy(start);
    	t.setStopDate(end);
    	if(motivation!=null) {
    		TherapyMotivation tmotiv = new TherapyMotivation(motivation);
    		t.setTherapyMotivation(tmotiv);
    	}
    	
	    	for (int i = 0; i < medicinsList.size(); i++) {
	    		DrugGeneric dg = medicinsList.get(i);
	    		if(rtvb) {
	    			DrugGeneric boostedDrug = locateDrugGeneric(dg.getGenericId()+"/r","");
	    			if(boostedDrug!=null) {
	    				dg = boostedDrug;
	    			}
	    		}
	    		TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, dg),
	    		                                        1.0, 
	    		                                        false,
	    		                                        false, 
	    		                                        (long)Frequency.DAYS.getSeconds());
	    		t.getTherapyGenerics().add(tg);
	    	}
	}

	private int getGenericDrugs(Map<Integer, DrugGeneric> drugMapping) throws SQLException {
		ResultSet rs = exportDb_.getDb().executeQuery("SELECT * FROM Compounds");
		int rtvb = -1;
		while(rs.next()) {
			int compoundId = rs.getInt("compoundID");
			String abbrev = rs.getString("abbreviation");
			String generic_name = rs.getString("generic_name");
			
			if(!abbrev.equals("RTVB")) {
				DrugGeneric dg = locateDrugGeneric(abbrev, generic_name);
				if(dg==null) {
					String mapping = mappings_.getMapping("drugs.mapping", abbrev);
					dg = locateDrugGeneric("", mapping);
				}
				if(dg==null) {
                    ConsoleLogger.getInstance().logWarning(
                            "Cannot map drug: " + abbrev + " - " + generic_name);
				} else {
					drugMapping.put(compoundId, dg);
				}
			} else {
				rtvb = compoundId;
			}
		}
		return rtvb;
	}
	
	private DrugGeneric locateDrugGeneric(String abbrev, String name) {
		if(name==null) {
			return null;
		}
		
		for(DrugGeneric dg : regaDrugGenerics) {
			if(dg.getGenericName().equals(name.toLowerCase())) {
				return dg;
			} else if(dg.getGenericId().equals(abbrev)) {
				return dg;
			}
 		}
		
		return null;
	}
}
