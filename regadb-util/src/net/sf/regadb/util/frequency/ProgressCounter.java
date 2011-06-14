package net.sf.regadb.util.frequency;

public class ProgressCounter extends TickCounter {
	
	private long targetCount;
	
	public ProgressCounter(long targetCount){
		super();
		
		this.targetCount = targetCount;
	}
	
	public double getProgressPercentage(){
		return (getTotal() * 100) / targetCount; 
	}

	public double getAvgTimeRemaining(){
		long rticks = targetCount - total;
		return rticks / getAvgRate();
	}
	
	public double getTimeRemaining(){
		long rticks = targetCount - total;
		return rticks / getRate();
	}
	
	public String getProgressString(){
		return getTotal() +"/"+ targetCount +" ("+ Math.round(getProgressPercentage()) +") "+ toHumanString(getTimeRemaining());
	}
	
	public static String toHumanString(double seconds){
		StringBuilder sb = new StringBuilder();
		
		double a = seconds / Frequency.secYear;
		if(a >= 1){
			a = Math.floor(a);
			sb.append(" "+ a +" "+ Frequency.strYear);
			seconds -= a * Frequency.secYear;
		}
		
		a = seconds / Frequency.secMonth;
		if(a >= 1){
			a = Math.floor(a);
			sb.append(" "+ a +" "+ Frequency.strMonth);
			seconds -= a * Frequency.secMonth;
		}
		
		a = seconds / Frequency.secDay;
		if(a >= 1){
			a = Math.floor(a);
			sb.append(" "+ a +" "+ Frequency.strDay);
			seconds -= a * Frequency.secDay;
		}
		
		a = seconds / Frequency.secHour;
		if(a >= 1){
			a = Math.floor(a);
			sb.append(" "+ a +" "+ Frequency.strHour);
			seconds -= a * Frequency.secHour;
		}
		
		a = seconds / Frequency.secMinute;
		if(a >= 1){
			a = Math.floor(a);
			sb.append(" "+ a +" "+ Frequency.strMinute);
			seconds -= a * Frequency.secMinute;
		}
		
		sb.append(" "+ seconds +" "+ Frequency.strSecond);
		
		return sb.toString();
	}
}
