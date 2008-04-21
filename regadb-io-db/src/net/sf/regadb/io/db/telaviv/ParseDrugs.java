package net.sf.regadb.io.db.telaviv;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Utils;

public class ParseDrugs extends Parser{
    
    public ParseDrugs(Logging logger){
        super(logger,null);
        setName("Drugs");
    }
    
    public Map<String,Drug> run(File drugsMappingFile){
        logInfo("Parsing drugs...");
        
        if(!check(drugsMappingFile))
            return null;
        
        Table mapTable = Utils.readTable(drugsMappingFile.getAbsolutePath());
        Map<String,Drug> map = new HashMap<String, Drug>();
        
        int CAgentNo= mapTable.findColumn("AgentNo");
        int CType   = mapTable.findColumn("type");
        int CMapping= mapTable.findColumn("mapping");
        
        for(int i = 1; i < mapTable.numRows(); ++i){
            String agentNo = mapTable.valueAt(CAgentNo, i);
            String type    = mapTable.valueAt(CType, i);
            String mapping = mapTable.valueAt(CMapping, i);
            
            if(check(mapping) && check(type) && check(agentNo)){
                Drug d = new Drug();
                
                if("0".equals(type)){
                    d.setType(DrugType.UNKNOWN);
                }
                else if("1".equals(type)){
                    d.setType(DrugType.GENERIC);
                    d.setDrug(new DrugGeneric(null,mapping,null));
                }
                else if("2".equals(type)){
                    d.setType(DrugType.COMMERCIAL);
                    d.setDrug(new DrugCommercial(mapping));
                }
                
                map.put(agentNo, d);
            }
        }
        
        return map;
    }

    static enum DrugType{UNKNOWN,GENERIC,COMMERCIAL};

    public class Drug{
        DrugType type = DrugType.UNKNOWN;
        private Object drug = null;
        
        public Drug(){
            
        }
        
        public Drug(DrugType type, Object drug){
            this.type = type;
            this.drug = drug;
        }
        
        public DrugType getType(){
            return type;
        }
        
        public Object getDrug(){
            return drug;
        }
        
        public void setType(DrugType type){
            this.type = type;
        }
        
        public void setDrug(Object drug){
            this.drug = drug;
        }
    }
}
