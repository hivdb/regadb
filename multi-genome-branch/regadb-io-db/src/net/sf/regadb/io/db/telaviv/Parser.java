package net.sf.regadb.io.db.telaviv;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.Logging;

public class Parser {
	protected SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy"); 
		
    private Logging logger=null;
    private List<DateFormat> dateFormats=new ArrayList<DateFormat>();
    private String name=null;
    
    private File currentFile=null;
    
    public Parser(){
        
    }
    
    public Parser(Logging logger,DateFormat df){
        setLogger(logger);
        setDateFormat(df);
    }
    
    public Parser(Logging logger,List<DateFormat> dfs){
        setLogger(logger);
        setDateFormats(dfs);
    }
    
    public List<DateFormat> getDateFormats(){
        return dateFormats;
    }
    
    public void setDateFormats(List<DateFormat> dateFormats){
        this.dateFormats = dateFormats;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public File getCurrentFile(){
        return currentFile;
    }
    
    public void setCurrentFile(File currentFile){
        this.currentFile = currentFile;
    }
    
    public Logging getLogger(){
        return logger;
    }
    
    public void setLogger(Logging logger){
        this.logger = logger;
    }
    
    protected void logInfo(String msg){
        getLogger().logInfo(formatMessage(msg));
    }
    
    protected void logWarn(String msg){
        getLogger().logWarning(formatMessage(msg));
    }
    
    protected void logErr(String msg){
        getLogger().logError(formatMessage(msg));
    }
    
    protected void logInfo(String msg, Object o){
        logInfo(formatMessage(msg,o));
    }
    
    protected void logWarn(String msg, Object o){
        logWarn(formatMessage(msg,o));
    }
    
    protected void logErr(String msg, Object o){
        logErr(formatMessage(msg,o));
    }

    
    protected void logInfo(String msg, File file, int line){
        logInfo(formatMessage(msg,file,line));
    }
    
    protected void logWarn(String msg, File file, int line){
        logWarn(formatMessage(msg,file,line));
    }
    
    protected void logErr(String msg, File file, int line){
        logErr(formatMessage(msg,file,line));
    }
    
    protected void logInfo(String msg, File file, int line, Object o){
        logInfo(formatMessage(msg,file,line,o));
    }
    
    protected void logWarn(String msg, File file, int line, Object o){
        logWarn(formatMessage(msg,file,line,o));
    }
    
    protected void logErr(String msg, File file, int line, Object o){
        logErr(formatMessage(msg,file,line,o));
    }
    
    protected void logErr(Patient p, String msg, Object o){
        logErr(formatMessage(p,msg,o));
    }
    
    protected void logInfo(Patient p, String msg, Object o){
        logInfo(formatMessage(p,msg,o));
    }
    
    protected void logWarn(Patient p, String msg, Object o){
        logWarn(formatMessage(p,msg,o));
    }
    protected void logInfo(Patient p, String msg, File file, int line){
        logInfo(formatMessage(p,msg,file,line));
    }
    
    protected void logWarn(Patient p, String msg, File file, int line){
        logWarn(formatMessage(p,msg,file,line));
    }
    
    protected void logErr(Patient p, String msg, File file, int line){
        logErr(formatMessage(p,msg,file,line));
    }
    
    protected void logInfo(Patient p, String msg, File file, int line, Object o){
        logInfo(formatMessage(p,msg,file,line,o));
    }
    
    protected void logWarn(Patient p, String msg, File file, int line, Object o){
        logWarn(formatMessage(p,msg,file,line,o));
    }
    
    protected void logErr(Patient p, String msg, File file, int line, Object o){
        logErr(formatMessage(p,msg,file,line,o));
    }


    private String formatMessage(Patient p, String msg, File file, int line, Object o){
        return formatMessage(p,formatMessage(msg,file,line,o));
    }
    
    private String formatMessage(Patient p, String msg, File file, int line){
        return formatMessage(p,formatMessage(msg,file,line));
    }
    
    private String formatMessage(Patient p, String msg, Object o){
        return formatMessage(p,formatMessage(msg,o));
    }

    private String formatMessage(Patient p, String msg){
        return "Patient ("+ p.getPatientId() +"): "+ msg;
    }

    
    private String formatMessage(String msg, File file, int line, Object o){
        return formatMessage(msg +": '"+ o +"'",file,line);
    }
    
    private String formatMessage(String msg, File file, int line){
        return (file.getName() +" ("+ line +"): "+ msg);
    }
    
    private String formatMessage(String msg, Object o){
        return (msg +": '"+ o +"'");
    }
    
    private String formatMessage(String msg){
        return getName() +": "+ msg; 
    }
    
    public DateFormat getDateFormat(){
        if(getDateFormats().size() > 0)
            return getDateFormats().get(0);
        return null;
    }
    
    public void setDateFormat(DateFormat df){
        getDateFormats().add(0, df);
    }
    
    public Date getDate(String date){
        Date d=null;
        
        for(DateFormat df : getDateFormats()){
            try{
                d = df.parse(date);
                if(d != null)
                    return d;
            }
            catch(Exception e){
            }
        }
        return d;
    }
    
    public boolean check(String s){
        return (s != null && s.length() > 0);
    }
    
    public boolean check(File f){
        if(f ==null || !f.exists()){
            logErr("File does not exist: "+ f);
            return false;
        }
        return true;
    }
}
