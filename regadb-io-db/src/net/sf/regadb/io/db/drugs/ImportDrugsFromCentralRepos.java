package net.sf.regadb.io.db.drugs;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.io.importXML.ImportDrugs;
import net.sf.regadb.service.wts.RegaDBWtsServer;

public class ImportDrugsFromCentralRepos {
    private static ImportDrugTransaction getDrugs() throws IOException {
        ImportDrugTransaction idt = new ImportDrugTransaction();
        
        try 
        {
            File drugClasses = RegaDBWtsServer.getDrugClasses();
            ImportDrugs.importDrugClasses(idt, drugClasses, false);
            drugClasses.delete();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        try 
        {
        	File drugGenerics = RegaDBWtsServer.getDrugGenerics();
            ImportDrugs.importGenericDrugs(idt, drugGenerics, false);
            drugGenerics.delete();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        try 
        {
            File drugCommercials = RegaDBWtsServer.getDrugCommercials();
            ImportDrugs.importCommercialDrugs(idt, drugCommercials, false);
            drugCommercials.delete();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return idt;
    }
    
    public List<DrugGeneric> getGenericDrugs() {
        try {
            ImportDrugTransaction idt = getDrugs();
            return idt.getDrugGeneric();
        } catch (IOException e) {
            return null;
        }
    }
    
    public List<DrugCommercial> getCommercialDrugs() {
        try {
            ImportDrugTransaction idt = getDrugs();
            return idt.getDrugCommercial();
        } catch (IOException e) {
            return null;
        }
    }
    
    public static void main(String [] args) {
        ImportDrugsFromCentralRepos idfcr = new ImportDrugsFromCentralRepos();
        
        List<DrugGeneric> dgs = idfcr.getGenericDrugs();
        for(DrugGeneric dg : dgs) {
            System.err.println(dg.getGenericName());
        }
        List<DrugCommercial> dcs = idfcr.getCommercialDrugs();
        for(DrugCommercial dc : dcs) {
            System.err.println(dc.getName());
        }
    }
}
