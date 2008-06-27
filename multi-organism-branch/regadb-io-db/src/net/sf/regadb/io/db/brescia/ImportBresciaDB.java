package net.sf.regadb.io.db.brescia;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalSymbolException;

import net.sf.regadb.align.Mutation;
import net.sf.regadb.csv.Table;
import net.sf.regadb.io.db.util.CreateCsvFiles;

public class ImportBresciaDB 
{
    private Table sampleTable;
    
    private int sampleTablePatientIdC;
    private int sampleTableDateC;
    private int sampleTableFlagC;
    private int sampleTableMutation;
    
    private Map<String, MutationSample> mutSamples = new HashMap<String, MutationSample>();
    
    private static String trugene_ref_aa_pro = 
        "---TLWQRPLVTIKIGGQLKEALLDTGADDTVLEEMSLPGRWKPKMIGGIGGFIKVRQYD" +
        "QILIEICGHKAIGTVLVGPTPVNIIGRNLLTQIGCTLNF";
     
    private static String trugene_ref_aa_rt = 
        "---------------------" +
        "---------------XCTEMEKEGKISKIGPENPYNTPVFAIKKKDSTKWRKLVDFRELN" +
        "KRTQDFWEVQLGIPHPAGLKKKKSVTVLDVGDAYFSVPLDEDFRKYTAFTIPSINNETPG" +
        "IRYQYNVLPQGWKGSPAIFQSSMTKILEPFRKQNPDIVIYQYMDDLYVGSDLEIGQHRTK" +
        "IEELRQHLLRWGLTTPDKKHQKEPPFLWMGYELHPDKWTVQPIVLP--------------" +
        "------------------------------------------------------------" +
        "------------------------------------------------------------" +
        "------------------------------------------------------------" +
        "------------------------------------------------------------" +
        "-----------------------------------------------------------";

    public ImportBresciaDB(String sampleFileName) throws FileNotFoundException
    {
        System.err.println("Reading data...");
        sampleTable = readTable(sampleFileName);
        sampleTablePatientIdC = findColumn(sampleTable, "ID_Coorte");
        sampleTableDateC = findColumn(sampleTable, "Data");
        sampleTableFlagC = findColumn(sampleTable, "Flag");
        sampleTableMutation = findColumn(sampleTable, "Mutazione");
        System.err.println("done.");
        
        parseSamples();
    }
    
    public void parseSamples()
    {
        String patientId;
        String date;
        String mutation;
        
        MutationSample mutSample;
        for(int i = 1; i<sampleTable.numRows(); i++)
        {
            patientId = sampleTable.valueAt(sampleTablePatientIdC, i);
            date = sampleTable.valueAt(sampleTableDateC, i);
            mutSample = mutSamples.get(patientId+date);
            if(mutSample==null)
            {
                mutSample = new MutationSample();
                mutSample.id_ = patientId;
                mutSample.date_ = parseDate(date, '/');
                mutSamples.put(patientId+date, mutSample);
            }
            mutation = sampleTable.valueAt(sampleTableMutation, i);
            mutation = parseMutation(mutation, i, patientId, date, sampleTable.valueAt(sampleTableFlagC, i));
            if(mutation!=null)
            {
                mutSample.mutations_.add(mutation);
            }
        }
        
        String aa;
        String codon;
        Set<String> possibleMPCodons = AaToNt.getMixedPopulationCodonTable();
        Set<String> possibleSimpleCodons = AaToNt.getSimpleCodonTable();
        int counter = 0;
        int noCodonMatch = 0;
        for(Map.Entry<String, MutationSample> e : mutSamples.entrySet())
        {
            for(String mut : e.getValue().mutations_)
            {
                //checking positions
                String protein = mut.split(",")[2];
                int pos = Integer.parseInt(mut.split(",")[0]);
                
                char aa_at_pos = '*';
                try {
                    aa_at_pos = '*';
                    if("PRO".equals(protein)) {
                        aa_at_pos = trugene_ref_aa_pro.charAt(pos-1);
                    } else if("RT".equals(protein)) {
                        aa_at_pos = trugene_ref_aa_rt.charAt(pos-1);
                    }
                    if(aa_at_pos=='-')
                        System.err.println("position not available in trugene ref seq" + mut);
                } catch (StringIndexOutOfBoundsException sioobex) {
                    System.err.println("ai -> " + mut);
                }
                //checking positions
                
                aa = mut.split(",")[1];
                if(aa.length()>1)
                    codon = AaToNt.findCodon(aa, possibleMPCodons);
                else
                    codon = AaToNt.findCodon(aa, possibleSimpleCodons);
                counter++;

                if(codon==null) {
                    codon = AaToNt.findCodon(aa+aa_at_pos, possibleMPCodons);
                    if(codon==null) {
                        System.err.println("cannot translate aa to codon" + aa + " wildtype " + aa_at_pos);
                        noCodonMatch++;
                    }
                }
                
                if(counter==1000)
                {
                    //System.err.print("*");
                    counter = 0;
                }
            }
        }
        System.err.println("noCodonMatch:"+noCodonMatch);
    }
    
    private String parseMutation(final String mutation, int pos, String patient_id, String date, String protein)
    {
        String temp = "";
        char ctemp;
        //strip non digit chars
        int countNonDigits = 0;
        for(int i = 0; i<mutation.length(); i++)
        {
            if(!Character.isDigit(mutation.charAt(i)))
            {
                countNonDigits++;
            }
            else
            {
                break;
            }
        }
        
        if(countNonDigits == mutation.length())
            {
            System.err.println("No position found in mutation: " + mutation + " in row "+pos +" -> ignoring (" + patient_id+","+date+")");
            return null;
            }
        
        temp = mutation.substring(countNonDigits);
        
        int countDigits = 0;
        String location = "";
        for(int i = 0; i<temp.length(); i++)
        {
            if(Character.isDigit(temp.charAt(i)))
            {
                location += temp.charAt(i);
                countDigits++;
            }
            else
            {
                break;
            }
        }
        
        temp = temp.substring(countDigits);
        
        String tempGarbage = "";
        String aminoAcids = "";

        for(int i = 0; i<temp.length(); i++)
        {
            if(Character.isLetter(temp.charAt(i)))
            {
                aminoAcids += temp.charAt(i);
            }
            else
            {
                tempGarbage += temp.charAt(i);
            }
        }
        
        boolean unparsable = false;
        for(int i = 0; i<tempGarbage.length(); i++)
        {
            if(tempGarbage.charAt(i)!='/' && tempGarbage.charAt(i)!=' ')
            {
                unparsable = true;
                break;
            }
        }
        
        if(unparsable)
        {
            System.err.println("Cannot parse mutation: " + mutation + " in row" + pos +" -> ignoring (" + patient_id+","+date+")");
            return null;
        }
        
        String prot;
        if("P".equals(protein))
        {
            prot = "PRO";
        }
        else if("T".equals(protein))
        {
            prot = "RT";
        }
        else
        {
            System.err.println("Cannot parse protein: " + protein + " in row" + pos +" -> ignoring (" + patient_id+","+date+") ->" + protein);
            return null;
        }
        
        //System.err.println(mutation + " -> " + location+","+aminoAcids+","+prot);
        
        return location+","+aminoAcids.toUpperCase()+","+prot;
    }
    
    private Date parseDate(String date, char dateSeparator)
    {
        if("".equals(date))
            return null;
        
        String dateNoTime = date.split(" ")[0];
        String [] dateTokens = dateNoTime.split(""+dateSeparator);
       
        return createDate(dateTokens[2], dateTokens[1], dateTokens[0]);
    }
    
    private static Date createDate(String yearStr, String monthStr, String dayString) {
        Calendar cal = Calendar.getInstance();

        if (!yearStr.equals("")) {
            int year, month;

            year = Integer.parseInt(yearStr);
            if (year < 1900)
                return null;

            if (!monthStr.equals("")) {
                month = Integer.parseInt(monthStr);
            } else
                month = 0;

            int day = 1;
            if(dayString!=null)
                day = Integer.parseInt(dayString);
            cal.set(year, month, day);
            return new Date(cal.getTimeInMillis());
        } else {
            return null;
        }
    }
    
    int findColumn(Table t, String name) {
        int column = t.findInRow(0, name);
        
        if (column == -1)
            throw new RuntimeException("Could not find column " + name);

        return column;
    }
    
     private Table readTable(String filename) throws FileNotFoundException {
        System.err.println(filename);
        return new Table(new BufferedInputStream(new FileInputStream(filename)), false);
    }
     
     public static void main(String [] args)
     {
         try {
             CreateCsvFiles.generateCsvFiles("/home/plibin0/it_import/");
            ImportBresciaDB ibdb = new ImportBresciaDB("/home/plibin0/it_import/Tab5_TestRes.csv");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     }
}
