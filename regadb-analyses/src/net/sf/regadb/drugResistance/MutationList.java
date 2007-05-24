package net.sf.regadb.drugResistance;

import java.util.List;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

public class MutationList 
{
    public static String getMutationLists(ViralIsolate isolate) 
    {
        StringBuffer result = new StringBuffer();
        
        for(NtSequence seq : isolate.getNtSequences())
        {
            StringBuffer resultRT = new StringBuffer();
            StringBuffer resultPRO = new StringBuffer();
            
            for (AaSequence aaseq : seq.getAaSequences()) 
            {
                if (aaseq.getProtein().getAbbreviation().equals("RT")) 
                {
                    getHivdbMutationList(aaseq, resultRT);
                }
                else if(aaseq.getProtein().getAbbreviation().equals("PRO"))
                {
                    getHivdbMutationList(aaseq, resultPRO);
                }
            }
            
            result.append(seq.getNtSequenceIi() + "{");
            result.append(resultRT);
            result.append("} {");
            result.append(resultPRO);
            result.append("}\n");
        }

        return result.toString();
    }
    
    public static void getHivdbMutationList(AaSequence aaSequence, StringBuffer buffer) 
    {
        for(AaMutation mut : aaSequence.getAaMutations())
        {
            buffer.append(" " + mut.getId().getPosition() + mut.getAaMutation());
        }
        
        for(AaInsertion ins : aaSequence.getAaInsertions())
        {
            buffer.append(" " + ins.getId().getPosition() + "i");
        }
    }
    
    public static void main(String [] args)
    {
        Login login = null;
            try
            {
                login = Login.authenticate("kdforc0", "Vitabis1");
            }
            catch (WrongUidException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (WrongPasswordException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            catch (DisabledUserException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (login == null) {
                throw new RuntimeException("Could not login with given username/password.");
            }
        Transaction t = login.createTransaction();
        
        SettingsUser settings = t.getSettingsUser();

        t.commit();
        
        t = login.createTransaction();
        
        List<Patient> p = t.getPatients();
        Patient ppp = null;
        for(Patient pp : p)
        {
            if(pp.getFirstName()!=null)
            if(pp.getFirstName().equals("Flapfooabc"))
            ppp = pp;
        }
        
        
        int index = 0;
        for(ViralIsolate vi : ppp.getViralIsolates())
            {
            if(index==1)
            {
                String a = getMutationLists(vi);
                System.err.println(a);
                
            }
            index++;
            //System.err.println("---");
            }
        
        System.err.println("done");
        t.commit();
    }
}
