package net.sf.regadb.ui.form.singlePatient;


import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.util.settings.ViralIsolateFormConfig;
import net.sf.regadb.util.settings.ViralIsolateFormConfig.ScoreInfo;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.webtoolkit.jwt.WColor;
import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateFormUtils {
	private final static Map<String,List<String>> proteinAbbrevDrugClass = new HashMap<String,List<String>>();
	static{
		List<String> l = new LinkedList<String>();
		l.add("PI");
		proteinAbbrevDrugClass.put("PR", l);
		
		l = new LinkedList<String>();
		l.add("NRTI");
		l.add("NNRTI");
		proteinAbbrevDrugClass.put("RT", l);
		
		l = new LinkedList<String>();
		l.add("EI");
		proteinAbbrevDrugClass.put("gp41", l);
		proteinAbbrevDrugClass.put("gp120", l);
		
		l = new LinkedList<String>();
		l.add("INI");
		proteinAbbrevDrugClass.put("IN", l);
	}
	
	public static Collection<String> getRelevantDrugClassIds(Collection<String> proteinAbbreviations){
		List<String> drugClasses = new ArrayList<String>();
        for(String proteinAbbreviation : proteinAbbreviations){
        	List<String> iis = proteinAbbrevDrugClass.get(proteinAbbreviation);
        	if(iis != null)
        		drugClasses.addAll(iis);
        }
        return drugClasses;
	}
	
    public static void putResistanceTableResult(TestResult tr, final WTableCell cell, final ViralIsolateFormConfig config, boolean onlyIfCurrentValueIsNA, final boolean canShowMutations)
    {
        //make sure we do not override a sensible value
        if(onlyIfCurrentValueIsNA) {
            if(!((WText)cell.getChildren().get(0)).getText().getValue().equals("NA"))
                return;
        }
        
        cell.clear();
        
        final WText toReturn = new WText("");
        final WText mutation = new WText("");
        
        if(tr==null)
        {
            toReturn.setText("NA");
            cell.getDecorationStyle().setForegroundColor(convert(Color.white));
            cell.getDecorationStyle().setBackgroundColor(convert(Color.black));
        }
        else
        {
            ResistanceInterpretationParser inp = new ResistanceInterpretationParser() {
                @Override
                public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) {
                    mutations = combineMutations(mutations);
                    
                    ScoreInfo si = config.getScoreInfo(gss);
                    toReturn.setText(si.getStringRepresentation());
                    cell.getDecorationStyle().setForegroundColor(convert(si.getColor()));
                    cell.getDecorationStyle().setBackgroundColor(convert(si.getBackgroundColor()));
                    
                    if(remarks!=null && !remarks.equals("null")) {
                    	cell.setStyleClass(cell.getStyleClass() + " resistance-remarks");
                        toReturn.setText(toReturn.getText() + "*");
                        
                    	toReturn.setToolTip(remarks);
                        cell.setToolTip(remarks);
                    }
                    if(canShowMutations && mutations.size()>0) {
                        StringBuffer currentValue = new StringBuffer();
                        currentValue.append(" (");
                        for(String mut : mutations) {
                            currentValue.append(mut + " ");
                        }
                        currentValue.replace(currentValue.length()-1, currentValue.length(), ")");
                        mutation.setText(currentValue.toString());
                        mutation.setStyleClass("mutations");
                    }
                }
            };
            try {
                inp.parse(new InputSource(new ByteArrayInputStream(tr.getData())));
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cell.setStyleClass("resistance-cell " + cell.getStyleClass());
        cell.addWidget(toReturn);
        if (!UIUtils.keyOrValue(mutation.getText()).equals("")) {
            cell.addWidget(mutation);
        }
    }
    
    public static WColor convert(Color c) {
    	return new WColor(c.getRed(), c.getGreen(), c.getBlue());
    }
    
    public static String getFixedGenericId(TestResult tr) {
        String genericId = tr.getDrugGeneric().getGenericId();
        if(genericId.startsWith("APV"))
            return genericId.replace("APV", "FPV");
        else
            return genericId;
    }
    
    public static ArrayList<String> combineMutations(ArrayList<String> mutations){
        Map<String,StringBuilder> positions = new HashMap<String,StringBuilder>();
        
        for(String mut : mutations){
            StringBuilder pre = new StringBuilder();
            StringBuilder pos = new StringBuilder();
            StringBuilder suf = new StringBuilder();
            
            for(int i=0; i<mut.length(); ++i){
                char c = mut.charAt(i);
                if(Character.isDigit(c))
                    pos.append(c);
                else if(pos.length() > 0)
                    suf.append(c);
                else
                    pre.append(c);
            }
            
            if(pos.length() > 0){
                StringBuilder sb = positions.get(pos.toString());
                if(sb == null)
                    positions.put(pos.toString(), new StringBuilder(mut));
                else
                    sb.append(suf);
            }
        }
        
        ArrayList<String> r = new ArrayList<String>();
        for(Map.Entry<String, StringBuilder> pos : positions.entrySet())
            r.add(pos.getValue().toString());
        return r;
    }
}
