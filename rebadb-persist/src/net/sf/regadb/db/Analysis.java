package net.sf.regadb.db;

public class Analysis 
{
    private String className;
    private String description;
    
    public Analysis(String className, String description)
    {
        this.className = className;
        this.description = description;
    }
    
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
