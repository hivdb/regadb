package net.sf.regadb.util.frequency;


public class Frequency{
    private static double secMinute = 60;
    private static double secHour = secMinute * 60;
    private static double secDay = secHour * 24;
    private static double secWeek = secDay * 7;
    private static double secMonth = secDay * 30.5;
    private static double secYear = secDay * 365;
    
    private static String strSecond = "second";
    private static String strMinute = "minute";
    private static String strHour = "hour";
    private static String strDay = "day";
    private static String strWeek = "week";
    private static String strMonth = "month";
    private static String strYear = "year";
        
    public static Frequency SECONDS    = new Frequency(strSecond, 1);
    public static Frequency MINUTES    = new Frequency(strMinute, secMinute);
    public static Frequency HOURS      = new Frequency(strHour, secHour);
    public static Frequency DAYS       = new Frequency(strDay, secDay);
    public static Frequency WEEKS      = new Frequency(strWeek, secWeek);
    public static Frequency MONTHS     = new Frequency(strMonth, secMonth);
    public static Frequency YEARS      = new Frequency(strYear, secYear);
    
    public static InverseFrequency PERSECONDS = new InverseFrequency(strSecond, 1);
    public static InverseFrequency PERMINUTES = new InverseFrequency(strMinute, secMinute);
    public static InverseFrequency PERHOURS = new InverseFrequency(strHour, secHour);
    public static InverseFrequency PERDAYS = new InverseFrequency(strDay, secDay);
    public static InverseFrequency PERWEEKS = new InverseFrequency(strWeek, secWeek);
    public static InverseFrequency PERMONTHS = new InverseFrequency(strMonth, secMonth);
    public static InverseFrequency PERYEARS = new InverseFrequency(strYear, secYear);


    private double seconds;
    private String unit;
    private double x=0;
 
    protected Frequency(){
        
    }
        
    protected Frequency(String unit, double seconds){
        this.unit = unit;
        this.seconds = seconds;
    }
    
    public double to(Frequency unit, double duration){
        return (seconds*duration / unit.getSeconds());
    }
    
    public double a(Frequency unit, double interval){
        return (unit.getSeconds()/(interval*seconds));
    }

    public double timesAUnitToInterval(Frequency unit, double times){
        return unit.getSeconds()/(times * seconds);
    }
    
    public double timesToInterval(double times){
        return seconds/times;
    }
    
    public double getSeconds(){
        return seconds;
    }
    protected void setSeconds(double seconds){
        this.seconds = seconds;
    }
    
    public String getUnit(){
        return unit;
    }
    private void setUnit(String unit){
        this.unit = unit;
    }

    public double getX(){
        return x;
    }
    public void setX(double x){
        this.x = x;
    }
    
    public String toString(){
        return "x a "+ getUnit();
    }
    
    public static String toString(double seconds){
        Frequency f = getFrequency(seconds);
        return f.getX() + " ("+ f.toString() +")"; 
    }
    
    public static Frequency getFrequency(double seconds){
        if(seconds != 0){
            double i=0;
            Frequency unit=null;
            double tolerance = 0.01;
            
            if ((i = Frequency.SECONDS.a(DAYS,seconds)) >= 1 && (i % 1 <= tolerance)){
                unit = DAYS;
            }
            else if ((i = Frequency.SECONDS.a(WEEKS,seconds)) >= 1 && (i % 1 <= tolerance)){
                unit = WEEKS;
            }
            else if ((i = Frequency.SECONDS.a(MONTHS,seconds)) >= 1 && (i % 1 <= tolerance)){
                unit = MONTHS;
            }
            else if ((i = Frequency.SECONDS.a(YEARS,seconds)) >= 1 && (i % 1 <= tolerance)){
                unit = YEARS;
            }
            else if ((i = seconds/secYear) >= 1 && (i % 1 <= tolerance)){
                unit = PERYEARS;
            }
            else if((i = seconds/secMonth) >= 1 && (i % 1 <= tolerance)){
                unit = PERMONTHS;
            }
            else if((i = seconds/secWeek) >= 1 && (i % 1 <= tolerance)){
                unit = PERWEEKS;
            }
            else if((i = seconds/secDay) >= 1 && (i % 1 <= tolerance)){
                unit = PERDAYS;
            }
            else{
                unit = PERSECONDS;
                i = seconds;
            }
            
            if(unit != null){
                try{
                    Frequency f;
                    if(unit instanceof InverseFrequency) {
                        f = new InverseFrequency(unit.getUnit(), unit.getSeconds());
                    }
                    else
                        f = new Frequency(unit.getUnit(), unit.getSeconds());
                    
                    f.setX(i);
                    return f;
                }
                catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
    
//  public static String toString(double seconds){
//  double i=0;
//  String unit="";
//  double tolerance = 0.01;
//  if((i = Frequency.SECONDS.aSecond(seconds)) >= 1 && (i % 1 <= tolerance)){
//      unit = "second";
//  }
//  else if ((i = Frequency.SECONDS.aMinute(seconds)) >= 1 && (i % 1 <= tolerance)){
//      unit = "minute";
//  }
//  else if ((i = Frequency.SECONDS.anHour(seconds)) >= 1 && (i % 1 <= tolerance)){
//      unit = "hour";
//  }
//  else if ((i = Frequency.SECONDS.aDay(seconds)) >= 1 && (i % 1 <= tolerance)){
//      unit = "day";
//  }
//  else if ((i = Frequency.SECONDS.aWeek(seconds)) >= 1 && (i % 1 <= tolerance)){
//      unit = "week";
//  }
//  else if ((i = Frequency.SECONDS.aMonth(seconds)) >= 1 && (i % 1 <= tolerance)){
//      unit = "month";
//  }
//  else if ((i = Frequency.SECONDS.aYear(seconds)) > 0){
//      unit = "year";
//  }
//  
//  return (long)i +" /"+ unit;
//}
//
//public static String toString2(double seconds){
//  String sunit[]  = new String[]  {"year", "month", "week", "day", "hour", "minute", "second"};
//  double unit[]   = new double[]  {secYear, secMonth, secWeek, secDay, secMinute, 1};
//  double res[]    = new double[6];
//  
//  double remainder=seconds;
//  int hunit = -1;
//
//  for(int i=0; i<unit.length; ++i){
//      res[i] = java.lang.Math.floor( remainder / unit[i]);
//      remainder = remainder - (res[i] * unit[i]);
//      
//      if(res[i] != 0 && hunit != -1)
//          hunit = i;
//  }
//  
//  for(int i=unit.length; i>=hunit; --i){
//      
//  }
//  
//  return "";
//}

}
