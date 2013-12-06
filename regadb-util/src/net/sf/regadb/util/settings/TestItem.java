package net.sf.regadb.util.settings;

public class TestItem{
    public TestItem(){
    }
    public TestItem(String d){
        description = d;
    }
    public TestItem(String d, String o){
        description = d;
        organism = o;
    }
    public TestItem(String d, String o, String defaultValue, boolean noValueSelected){
        description = d;
        organism = o;
        this.defaultValue = defaultValue;
        this.noValueSelected = noValueSelected;
    }
    public String description = null;
    public String organism = null;
    public String defaultValue = null;
    public boolean noValueSelected = true;
}
