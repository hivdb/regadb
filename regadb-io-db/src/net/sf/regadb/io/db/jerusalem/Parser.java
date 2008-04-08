package net.sf.regadb.io.db.jerusalem;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import net.sf.regadb.io.db.util.Logging;

public class Parser {
    private Logging logger=null;
    private DateFormat df=null;
    private String name=null;
    
    private File currentFile=null;
    private int currentLineNumber=0;
    
    public Parser(){
        
    }
    
    public Parser(Logging logger,DateFormat df){
        setLogger(logger);
        setDateFormat(df);
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
        getLogger().logWarning(formatMessage(msg));
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
        return df;
    }
    
    public void setDateFormat(DateFormat df){
        this.df = df;
    }
    
    public Date getDate(String date){
        Date d=null;
        try{
            d = df.parse(date);
        }
        catch(Exception e){
            //logWarn(e.getMessage());
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
