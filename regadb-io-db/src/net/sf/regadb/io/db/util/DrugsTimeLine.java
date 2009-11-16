package net.sf.regadb.io.db.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.meta.Equals;

public class DrugsTimeLine {
	@SuppressWarnings("serial")
	public static class TimeLineException extends Exception{
		public TimeLineException(){
			super();
		}
		
		public TimeLineException(String msg){
			super(msg);
		}
	}
	@SuppressWarnings("serial")
	public static class DuplicateDrugsException extends TimeLineException{
	}
	
	private TreeMap<Date,Therapy> timeline = new TreeMap<Date,Therapy>();
	
	public void addDrugs(Date date, DrugGeneric drug, boolean blind, boolean placebo, Double dose, Long frequency) throws TimeLineException{
		Therapy t = timeline.get(date);
		if(t == null)
			t = insertTherapy(date);
		
		TherapyGeneric tg = getTherapyGeneric(t, drug);
		if(tg != null)
			throw new DuplicateDrugsException();
		
		tg = new TherapyGeneric();
		tg.setId(new TherapyGenericId(t,drug));
		tg.setBlind(blind);
		tg.setDayDosageMg(dose);
		tg.setPlacebo(placebo);
		tg.setFrequency(frequency);
		t.getTherapyGenerics().add(tg);
	}
	
	public void addDrugs(Date date, DrugCommercial drug, boolean blind, boolean placebo, Double units, Long frequency) throws TimeLineException{
		Therapy t = timeline.get(date);
		if(t == null)
			t = insertTherapy(date);
		
		TherapyCommercial tc = getTherapyCommercial(t, drug);
		if(tc != null)
			throw new DuplicateDrugsException();
		
		tc = new TherapyCommercial();
		tc.setId(new TherapyCommercialId(t,drug));
		tc.setBlind(blind);
		tc.setDayDosageUnits(units);
		tc.setPlacebo(placebo);
		tc.setFrequency(frequency);
		t.getTherapyCommercials().add(tc);
	}
	
	protected Therapy insertTherapy(Date startDate){
		Therapy t = new Therapy();
		t.setStartDate(startDate);
		
		SortedMap<Date,Therapy> submap = timeline.headMap(startDate);
		if(submap.size() > 0)
			submap.get(submap.lastKey()).setStopDate(startDate);
		
		submap = timeline.tailMap(startDate);
		if(submap.size() > 0)
			t.setStopDate(submap.get(submap.firstKey()).getStartDate());
		
		timeline.put(startDate, t);
		return t;
	}
	
	public Therapy getTherapy(Date startDate){
		return timeline.get(startDate);
	}

	public static TherapyCommercial getTherapyCommercial(Therapy t, DrugCommercial d){
		for(TherapyCommercial tc : t.getTherapyCommercials()){
			if(Equals.isSameDrugCommercial(d, tc.getId().getDrugCommercial()))
				return tc;
		}
		return null;
	}
	public static TherapyGeneric getTherapyGeneric(Therapy t, DrugGeneric d){
		for(TherapyGeneric tg : t.getTherapyGenerics()){
			if(Equals.isSameDrugGeneric(d, tg.getId().getDrugGeneric()))
				return tg;
		}
		return null;
	}
	
	public Collection<Therapy> getTherapies(){
		return timeline.values();
	}
	
	public Collection<Therapy> removeEmptyTherapies(){
		List<Date> remove = new ArrayList<Date>();
		List<Therapy> empties = new ArrayList<Therapy>();
		
		Therapy prev = null;
		for(Map.Entry<Date, Therapy> me : timeline.entrySet()){
			if(me.getValue().getTherapyCommercials().size() == 0
				|| me.getValue().getTherapyGenerics().size() == 0){
				if(prev != null)
					prev.setStopDate(me.getValue().getStartDate());
				
				empties.add(me.getValue());
				remove.add(me.getKey());
			}
			else{
				prev = me.getValue();
			}
		}
		
		for(Date d : remove)
			timeline.remove(d);
		
		return empties;
	}
	
	public void mergeTherapies(){
		List<Date> remove = new ArrayList<Date>();
		
		Therapy prev = null;
		for(Map.Entry<Date, Therapy> me : timeline.entrySet()){
			if(prev == null || !isSameTherapyRegiment(prev, me.getValue())){
				prev = me.getValue();
			}
			else{
				remove.add(me.getKey());
				prev.setStopDate(me.getValue().getStopDate());
			}
		}
		for(Date d : remove)
			timeline.remove(d);
	}
	
	public static boolean isSameTherapyRegiment(Therapy t1, Therapy t2){
		if(t1.getTherapyCommercials().size() != t2.getTherapyCommercials().size()
				|| t1.getTherapyGenerics().size() != t2.getTherapyGenerics().size())
			return false;
		
		for(TherapyCommercial tc1 : t1.getTherapyCommercials()){
			boolean found = false;
			for(TherapyCommercial tc2 : t2.getTherapyCommercials()){
				if(Equals.isSameTherapyCommercialEx(tc1, tc2)){
					found = true;
					break;
				}
			}
			if(!found)
				return false;
		}
		for(TherapyGeneric tg1 : t1.getTherapyGenerics()){
			boolean found = false;
			for(TherapyGeneric tg2 : t2.getTherapyGenerics()){
				if(Equals.isSameTherapyGenericEx(tg1, tg2)){
					found = true;
					break;
				}
			}
			if(!found)
				return false;
		}
		
		return true;
	}
}
