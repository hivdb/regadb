package net.sf.regadb.io.db.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.regadb.util.pair.Pair;

public class TimeLine<T> {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
    private TreeMap<Date, List<Period>> timeline = new TreeMap<Date, List<Period>>();
    
    public class Period{
        private List<T> values;
        private Date start;
        private Date stop;
        
        public Period(Date start, Date stop, List<T> values){
            setStart(start);
            setStop(stop);
            setValues(values);
        }
        
        public Period(Date start, Date stop, T value){
            setStart(start);
            setStop(stop);
            setValues(new ArrayList<T>());
            getValues().add(value);
        }

        public void setStart(Date start) {
            this.start = start;
        }

        public Date getStart() {
            return start;
        }

        public void setStop(Date stop) {
            this.stop = stop;
        }

        public Date getStop() {
            return stop;
        }

        public void setValues(List<T> values) {
            this.values = values;
        }

        public List<T> getValues() {
            return values;
        }
        
        public String toString(){
        	return (start == null ? "...":dateFormat.format(start)) +" -> "+ (stop == null ? "...":dateFormat.format(stop));
        }
        
        public boolean equals(Period p){
        	return (start == p.start || (start != null && start.equals(p.start)))
        		&& (stop == p.stop || (stop != null && stop.equals(p.stop)));
        }
    }
    
    public void addPeriod(Date start, Date end, T value){
        Period period = new Period(start, end, value);
        addPeriod(period);
    }
    
    public void addPeriod(Period period){
        List<Period> periods = getTimeline().get(period.getStart());
        if(periods != null){
            periods.add(period);
        }
        else{
            periods = new ArrayList<Period>();
            periods.add(period);
            getTimeline().put(period.getStart(),periods);
        }
        
        if(period.getStop() != null){
            periods = getTimeline().get(period.getStop());
            if(periods != null){
                periods.add(period);
            }
            else{
                periods = new ArrayList<Period>();
                periods.add(period);
                getTimeline().put(period.getStop(),periods);
            }
        }
    }
    
    public boolean overlap(Period p1, Period p2){
        return overlap(p1,p2,true);
    }
    public boolean overlap(Period p1, Period p2, boolean strict){
        return overlap(p1.getStart(),p1.getStop(),p2.getStart(),p2.getStop(),strict);
    }
    
    public static boolean overlap(Date start1, Date stop1, Date start2, Date stop2){
    	return overlap(start1, stop1, start2, stop2, true);
    }
    public static boolean overlap(Date start1, Date stop1, Date start2, Date stop2, boolean strict){
        if(stop2 != null){
        	if(start1.after(stop2))
        		return false;
        	
        	if(!strict && start1.equals(stop2))
        		return false;
        }
        if(stop1 != null){
        	if(start2.after(stop1))
        		return false;
        	
        	if(!strict && start2.equals(stop1))
        		return false;
        }
        
        return true;
    }
    
    public List<Period> createMergedPeriods(){
        List<Period> periods = new ArrayList<Period>();
        
        Set<Period> openPeriods = new HashSet<Period>();
        
        Date start = null;
        
        for(Date d : getTimeline().keySet()){
            if(start != null){
                List<T> values = getAllValues(openPeriods);
                periods.add(new Period(new Date(start.getTime()),new Date(d.getTime()),values));
            }
            
            for(Period period : getTimeline().get(d)){
                if(isStartDate(d, period)){
                    openPeriods.add(period);
                }
                else{
                    openPeriods.remove(period);
                }
            }
            
            if(openPeriods.size() > 0)
                start = d;
            else
                start = null;
        }
        if(openPeriods.size() > 0){
            List<T> values = getAllValues(openPeriods);
            periods.add(new Period(new Date(getTimeline().lastKey().getTime()),null,values));
        }
        
        return periods;
    }
    
    public List<T> getAllValues(Collection<Period> collection){
        List<T> all = new ArrayList<T>();
        
        for(Period p : collection){
            all.addAll(p.getValues());
        }
        
        return all;
    }
    
    public List<Pair<Period, List<Period>>> getOverlappingPeriods(boolean strict){
    	List<Pair<Period, List<Period>>> r = new ArrayList<Pair<Period,List<Period>>>();
    	
    	for(Map.Entry<Date,List<Period>> pl1 : timeline.entrySet()){
    		for(Period p1 : pl1.getValue()){
    			if(!p1.getStart().equals(pl1.getKey()))
    				continue;
    			
    			List<Period> overlaps = new ArrayList<Period>();

    			for(Period p2 : pl1.getValue())
    				if(p1 != p2 && p2.getStart().equals(pl1.getKey()))
    					overlaps.add(p2);
    			
    	    	for(Map.Entry<Date,List<Period>> pl2 : timeline.tailMap(pl1.getKey()).entrySet()){
    	    	    if(pl2.getKey().equals(pl1.getKey()))
    	    	        continue;
    	    	    
        			int size = overlaps.size();

    	    		for(Period p2 : pl2.getValue()){
    	    			if(!p2.getStart().equals(pl2.getKey()))
    	    				continue;
    	    			
    	    			if(overlap(p1, p2, strict))
    	    				overlaps.add(p2);
    	    		}

    	    		if(overlaps.size() == size)
        	    		break;
    	    	}
    	    	if(overlaps.size() > 0)
    	    		r.add(new Pair<Period, List<Period>>(p1,overlaps));
    		}
    	}
    	
    	return r;
    }
    
    public boolean isStartDate(Date d, Period p){
        return d.equals(p.getStart());
    }

    public void setTimeline(TreeMap<Date, List<Period>> timeline) {
        this.timeline = timeline;
    }

    public TreeMap<Date, List<Period>> getTimeline() {
        return timeline;
    }
}
