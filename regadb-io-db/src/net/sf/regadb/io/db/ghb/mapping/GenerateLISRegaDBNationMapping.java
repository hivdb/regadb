package net.sf.regadb.io.db.ghb.mapping;

import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.io.db.util.Utils;

public class GenerateLISRegaDBNationMapping {
    public static void main(String [] args) {
        Table nationalityLIS = Utils.readTable("/home/plibin0/myWorkspace/regadb-io-db/src/net/sf/regadb/io/db/ghb/mapping/LIS-nationality.csv");
        Table nationalityDefault = Utils.readTable("/home/plibin0/myWorkspace/regadb-analyses/io-assist-files/countrylist.csv");
        
        int CLISCode = Utils.findColumn(nationalityLIS, "codePost");
        int CLISName = Utils.findColumn(nationalityLIS, "internationaleOmschrijving");
        int CDefaultCode = Utils.findColumn(nationalityDefault, "ISO 3166-1 2 Letter Code");
        int CDefaultName = Utils.findColumn(nationalityDefault, "Common Name");
        List<Attribute> regadbAttributesList = Utils.prepareRegaDBAttributes();
        Attribute countryOfOrigin = Utils.selectAttribute("Country of origin", regadbAttributesList);
        
        for(int i = 1; i<nationalityLIS.numRows(); i++) {
            String LISCode = nationalityLIS.valueAt(CLISCode, i);
            String LISName = nationalityLIS.valueAt(CLISName, i);
            String match = "";
            for(int j = 1; j<nationalityDefault.numRows(); j++) {
                String defaultCode = nationalityDefault.valueAt(CDefaultCode, j);
                String defaultName = nationalityDefault.valueAt(CDefaultName, j);
                if(LISCode.trim().toLowerCase().equals(defaultCode.trim().toLowerCase())) {
                    match = defaultName;
                    break;
                }
            }
            
            System.err.println("\"" + LISCode + "\"" + "," + "\"" + match + "\"" );
        }
    }
}
