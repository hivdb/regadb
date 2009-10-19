package net.sf.regadb.io.db.ghb.filemaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.io.db.util.DrugsTimeLine;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.DrugsTimeLine.TimeLineException;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.frequency.Frequency;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ParseTherapy {
	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	private Map<String, DrugsTimeLine> timelines = new HashMap<String, DrugsTimeLine>();

	private Set<String> ignoredDrugs = new HashSet<String>();
	private Map<String, DrugGeneric> drugGenericMap = new HashMap<String, DrugGeneric>();
	private Map<String, DrugCommercial> drugCommercialMap = new HashMap<String, DrugCommercial>();
	
	private Set<String> distinctDrugs = new HashSet<String>();
	
	private TreeMap<String,Long> frequencies = new TreeMap<String,Long>();
	
	public Map<String, DrugsTimeLine> getDrugsTimeLines(){
		return timelines;
	}

	public void parse(File therapyFile, File mappingPath) throws IOException{
		loadIgnoredDrugs(new File(mappingPath.getAbsolutePath() + File.separatorChar +"drugsToIgnore.mapping"));
		loadDrugMaps(new File(mappingPath.getAbsolutePath() + File.separatorChar +"drugs.mapping"));
		
        Table therapyTable = Utils.readTable(therapyFile.getAbsolutePath(), ParseAll.getCharset(), ParseAll.getDelimiter());
        
        int cPatientId = Utils.findColumn(therapyTable, "Patient_ID");
        int cDate = Utils.findColumn(therapyTable, "Datum");
        int cMedication = Utils.findColumn(therapyTable, "Medicatie");
        int cDose = Utils.findColumn(therapyTable, "Dosis");
        int cAmount = Utils.findColumn(therapyTable, "Aantal_Dosissen");
        int cFrequency = Utils.findColumn(therapyTable, "Frekwentie");
        int cBlind = Utils.findColumn(therapyTable, "blind");
        
        Set<String> unmappedDrugs = new TreeSet<String>();
        
        for(int i=1; i<therapyTable.numRows(); ++i) {
        	String patientId = therapyTable.valueAt(cPatientId, i).trim();
        	if(patientId.length() == 0)
        		continue;
        	
        	DrugsTimeLine timeline = timelines.get(patientId);
        	if(timeline == null){
        		timeline = new DrugsTimeLine();
        		timelines.put(patientId, timeline);
        	}
        		
        	Date date;
        	String sDate = therapyTable.valueAt(cDate, i).trim();
        	try {
				 date = dateFormat.parse(sDate);
			} catch (ParseException e) {
				if(sDate.length() > 0)
					System.err.println("Invalid date: '"+ therapyTable.valueAt(cDate, i) +'\'');
				continue;
			}
			
        	String medication = therapyTable.valueAt(cMedication, i).trim();
        	String drug = getDrugFromMedication(medication);
        	if(ignoreDrug(drug))
        		continue;
        	
        	String sDose = therapyTable.valueAt(cDose, i).trim().replace(',','.');
        	Double dose = getDose(sDose);
        	Double amount = getAmount(therapyTable.valueAt(cAmount, i).trim());
        	Long frequency = getFrequency(therapyTable.valueAt(cFrequency, i).trim());
        	boolean blind = therapyTable.valueAt(cBlind, i).trim().equals("1");
        	boolean placebo = medication.toLowerCase().contains("placebo");
        	
        	if(!handleException(timeline, date, drug, sDose, dose, amount, frequency, blind, placebo)){
	        	DrugCommercial dc = getDrugCommercial(drug,sDose);
	        	if(dc != null){
	        		if(distinctDrugs.add(drug))
	        			System.err.println("Mapping: '"+ drug +"' ("+ dose +") -> '"+ dc.getName() +'\'');
	        		try {
						timeline.addDrugs(date, dc, blind, placebo, amount, frequency);
					} catch (TimeLineException e) {
					}
	        	}else{
	        		DrugGeneric dg = getDrugGeneric(drug);
	        		if(dg != null){
	        			if(distinctDrugs.add(drug))
	            			System.err.println("Mapping: '"+ drug +"' -> '"+ dg.getGenericName() +'\'');
	        			try {
							timeline.addDrugs(date, dg, blind, placebo, dose, frequency);
						} catch (TimeLineException e) {
						}
	        		}else{
	        			unmappedDrugs.add(drug);
	        		}
	        	}
        	}
        }
        
        if(unmappedDrugs.size() > 0){
        	System.err.println("Unmapped drugs:");
	        for(String s : unmappedDrugs)
	        	System.err.println('\''+ s +'\'');
        }
        
        for(Map.Entry<String, DrugsTimeLine> me : timelines.entrySet()){
        	me.getValue().mergeTherapies();
        }
	}
	
	private boolean handleException(DrugsTimeLine timeline, Date date, String drug,
			String sDose, Double dose, Double amount, Long frequency, boolean blind,
			boolean placebo) {
		if(drug.toLowerCase().startsWith("ziagen/epivir")){
			try {
				timeline.addDrugs(date, drugCommercialMap.get("ziagen"), blind, placebo, amount, frequency);
				timeline.addDrugs(date, drugCommercialMap.get("epivir"), blind, placebo, amount, frequency);
			} catch (TimeLineException e) {
			}
			return true;
		}
		return false;
	}

	private Double getDose(String dose){
		try{
			return Double.parseDouble(dose);
		}
		catch(NumberFormatException e){
			return null;
		}
	}
	
	private Double getAmount(String amount){
		try{
			return Double.parseDouble(amount.replace(',','.'));
		}
		catch(NumberFormatException e){
			return null;
		}
	}
	
	private Long getFrequency(String frequency) {
		Long f = frequencies.get(frequency);
		if(f == null){
			int p = frequency.indexOf('*');
			if(p != -1){
				Double x = Double.parseDouble(frequency.substring(0, p));
				
				if(frequency.endsWith("/ dag"))
					f = new Long((long)Frequency.DAYS.timesToInterval(x));
				else if(frequency.endsWith("/ 2 dg"))
					f = new Long((long)Frequency.PERDAYS.timesToInterval(2));
				else
					f = new Long((long)Frequency.getDefaultFrequency());
			}
			else
				f = new Long((long)Frequency.getDefaultFrequency());
			
			frequencies.put(frequency, f);
		}
		return f;
	}

	private String getDrugFromMedication(String medication) {
		return medication.replace("®", "");
	}

	private void loadIgnoredDrugs(File file) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		String line;
		while((line = in.readLine()) != null)
			ignoredDrugs.add(line.trim().toLowerCase());
		
		in.close();
	}
	
	private boolean ignoreDrug(String drug){
		return ignoredDrugs.contains(drug.toLowerCase());
	}
	
	/**
	 * @param drug
	 * @return first part of 'drug', trimmed and lowercase
	 */
	private String getDrugKey(String drug){
		drug = drug.trim().toLowerCase();
		
		int pos = drug.indexOf(' ');
		if(pos != -1)
			drug = drug.substring(0, pos);

		pos = drug.indexOf('.');
		if(pos != -1)
			drug = drug.substring(0, pos);
		
		return drug;
	}

	/**
	 * Tries to map the imported drug/dose using several variations:
	 * <ol>
	 * <li>complete name</li>
	 * <li>short name + dose</li>
	 * <li>short name</li>
	 * </ol>
	 * @param name imported name of the drug
	 * @param dose imported dose
	 * @return the DrugCommercial object mapped to name/dose or null if not found 
	 */
	private DrugCommercial getDrugCommercial(String name, String dose){
		DrugCommercial d = drugCommercialMap.get(name);
		if(d != null)
			return d;
		d = drugCommercialMap.get(getDrugKey(name) + dose);
		return d == null ? drugCommercialMap.get(getDrugKey(name)) : d;
	}
	
	private DrugGeneric getDrugGeneric(String name){
		DrugGeneric d = drugGenericMap.get(name);
		return d == null ? drugGenericMap.get(getDrugKey(name)) : d;
	}
	
	
	/**
	 * Fills the drug maps,
	 * for every commercial drug there is an entry with:
	 * - complete name
	 * - short name+dose (if dose exists)
	 * - short name
	 * for a generic drug:
	 * - complete genericName
	 * - short genericName
	 * 
	 * @param file drug.mapping file
	 * @throws IOException
	 */
	private void loadDrugMaps(File file) throws IOException{
		ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
        for(DrugCommercial d : imDrug.getCommercialDrugs()){
        	String a[] = d.getName().split(" ");
        	int i = getMgIndex(a);
        	if(i != -1)
        		drugCommercialMap.put(a[0].toLowerCase() + a[i],d);
        	drugCommercialMap.put(a[0].toLowerCase(), d);
            drugCommercialMap.put(d.getName(), d);
        }
        for(DrugGeneric d : imDrug.getGenericDrugs()){
            drugGenericMap.put(getDrugKey(d.getGenericName()),d);
            drugGenericMap.put(d.getGenericName(),d);
        }
        
        BufferedReader in = new BufferedReader(new FileReader(file));

        String line;
        while((line = in.readLine()) != null){
        	String map[] = line.replace("\"", "").replace("'", "").split(",");
        	if(map.length < 2)
        		continue;
        	
        	String from = map[0];
        	String to = map[1];
        	
        	DrugGeneric dg = drugGenericMap.get(to);
        	if(dg != null){
        		drugGenericMap.put(from, dg);
        		continue;
        	}
        	
        	DrugCommercial dc = drugCommercialMap.get(to);
        	if(dc != null){
        		drugCommercialMap.put(from, dc);
        		continue;
        	}
        	
        	System.err.println("Error in drug mapping file, drug '"+ to +"' does not exist.");
        }
        
        in.close();
	}
	
	private int getMgIndex(String name[]){
		for(int i=0; i<name.length; ++i){
			try{
				Double.parseDouble(name[i]);
				if(i <= name.length && name[i+1].equals("mg"))
					return i;
			}catch(Exception e){
				
			}
		}
		return -1;
	}
															
	public static void main(String args[]) throws IOException{
		Arguments as = new Arguments();
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		PositionalArgument therapyFile = as.addPositionalArgument("therapy-file", true);
		PositionalArgument mappingPath = as.addPositionalArgument("mapping-path", true);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		RegaDBSettings.createInstance();
		
		ParseTherapy ptdr = new ParseTherapy();
		ptdr.parse(new File(therapyFile.getValue()), new File(mappingPath.getValue()));

		for(Map.Entry<String, DrugsTimeLine> me : ptdr.timelines.entrySet()){
			System.out.println(me.getKey());
			
			for(Therapy t : me.getValue().getTherapies())
				System.out.println(ptdr.dateFormat.format(t.getStartDate()) +" - "
						+ (t.getStopDate() == null ? "...":ptdr.dateFormat.format(t.getStopDate()))
						+" "+ t.getTherapyCommercials().size() +" "+ t.getTherapyGenerics().size());
			
			System.out.println();
		}
	}
}
