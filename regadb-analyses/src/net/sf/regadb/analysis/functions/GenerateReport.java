package net.sf.regadb.analysis.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.regadb.db.TestResult;

public class GenerateReport 
{
    StringBuffer rtfBuffer_;
    
    public GenerateReport(byte[] rtfFileContent) 
    {
        rtfBuffer_ = new StringBuffer(new String(rtfFileContent));
    }
    
    public byte[] getReport() {        
        return rtfBuffer_.toString().getBytes();
    }
    
    public void replace(String find, String replace) {
        int indexOf = rtfBuffer_.indexOf(find);
        if(indexOf!=-1)
            rtfBuffer_.replace(indexOf, indexOf + find.length(), replace);
    }
    
    private void appendHexdump(File inputFile, StringBuffer toAppend) throws IOException {        
        FileInputStream fis = new FileInputStream(inputFile);
        for (int b = fis.read(); b != -1; b = fis.read()) {
            String hex = Integer.toHexString(b);
            if (hex.length() == 1)
                toAppend.append("0");
            toAppend.append(hex);
        }
    }

    public void writePicture(String find, File input) throws IOException {
        StringBuffer pic = new StringBuffer();
        pic.append(" }{\\*\\shppict{\\pict\\pngblip\n");
        appendHexdump(input, pic);
        pic.append("}}");
        int findStart = rtfBuffer_.indexOf(find);
        if(findStart!=-1)
            rtfBuffer_.replace(findStart, findStart + find.length(), pic.toString());
    }
    
    protected void addTables(/*LinkedHashMap DRUG_NAMES,*/ List<TestResult> testResults)
    {
        LinkedHashMap<String,String> DRUG_NAMES = new LinkedHashMap<String,String>();
        // NRTI
        DRUG_NAMES.put("zidovudine", "AZT");
        DRUG_NAMES.put("zalcitabine", "DDC");
        DRUG_NAMES.put("didanosine", "DDI");
        DRUG_NAMES.put("lamivudine", "3TC");
        DRUG_NAMES.put("stavudine", "D4T");
        DRUG_NAMES.put("abacavir", "ABC");
        DRUG_NAMES.put("emtricitabine", "FTC");
        DRUG_NAMES.put("tenofovir", "TDF");
        // NNRTI
        DRUG_NAMES.put("nevirapine", "NVP");
        DRUG_NAMES.put("delavirdine", "DLV");
        DRUG_NAMES.put("efavirenz", "EFV");
        DRUG_NAMES.put("etravirine", "ETV");
        // PI
        DRUG_NAMES.put("saquinavir", "SQV");
        DRUG_NAMES.put("saquinavir/r", "SQV/r");
        DRUG_NAMES.put("ritonavir", "RTV");
        DRUG_NAMES.put("indinavir", "IDV");
        DRUG_NAMES.put("indinavir/r", "IDV/r");
        DRUG_NAMES.put("nelfinavir", "NFV");
        DRUG_NAMES.put("amprenavir", "APV");
        DRUG_NAMES.put("amprenavir/r", "APV/r");
        DRUG_NAMES.put("fosamprenavir", "FPV");
        DRUG_NAMES.put("fosamprenavir/r", "FPV/r");
        DRUG_NAMES.put("lopinavir/r", "LPV/r"); 
        DRUG_NAMES.put("atazanavir", "ATV");
        DRUG_NAMES.put("atazanavir/r", "ATV/r");
        DRUG_NAMES.put("tipranavir/r", "TPV/r");
        DRUG_NAMES.put("darunavir/r", "DRV/r");
        // ENV
        DRUG_NAMES.put("enfuvirtide", "T20");
        
        int interpretation1Pos = rtfBuffer_.indexOf("$INTERPRETATION1");
        int mutation1Pos = rtfBuffer_.indexOf("$MUTATIONS1");
        
        String lastString;
        if (interpretation1Pos < mutation1Pos)
            lastString = "$MUTATIONS";
        else
            lastString = "$INTERPRETATION";
        
        String line
            = rtfBuffer_.substring( rtfBuffer_.indexOf(lastString + "2") + lastString.length() + 1,
                                    rtfBuffer_.indexOf(lastString + "3") + lastString.length() + 1);

        System.err.println(line);
        
        int ii = 1;
        TestResult tr;
        for (Iterator i = DRUG_NAMES.keySet().iterator(); i.hasNext();) {
            String drug = (String) i.next();
            String drugCode = (String) DRUG_NAMES.get(drug);

            String mutations = null;
            String interpretation = null;

            /*if (drugCode.equals("T20")) {
                mutations = lang.equals("NL") ? "niet bepaald" : "not determined";
                interpretation = "-";
            } else {*/
            tr = null;
            for(TestResult ttr : testResults) {
                if(drugCode.equals(ttr.getDrugGeneric().getGenericId())) {
                    tr = ttr;
                }
            }
               // ResistanceResultGeneric resistanceResult = drugForm.getResistanceResult(testII,drugCode);

                if (tr != null) {
                    //TODO mutations
                    //TODO interprete gss
                    mutations = "lala";//resistanceResult.getComment();
                    interpretation = tr.getValue(); //resistanceResult.getInterpretation(lang);
                }
            //}

            if (mutations != null) {
                if (ii >= 3)
                    replace(line, line + line);

                String reportMutations = "$MUTATIONS" + (Math.min(ii, 3));
                String reportInterpretation = "$INTERPRETATION" + (Math.min(ii, 3));

                replace("$DRUG" + (Math.min(ii, 3)), drug);
                replace(reportMutations, mutations);
                replace(reportInterpretation, interpretation);
                ++ii;
            }
        }
        
        replace(line, "");       
    }
}
