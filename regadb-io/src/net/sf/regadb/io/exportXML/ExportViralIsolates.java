package net.sf.regadb.io.exportXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.export.PatientExporter;
import net.sf.regadb.io.exportXML.ExportToXMLOutputStream.ViralIsolateXMLOutputStream;

public class ExportViralIsolates {
    private Login login;
    private String dataset;
    private File file;
    
    public static void main(String args[]){
        if(args.length < 4){
            System.err.println("Usage: ExportViralIsolates <output file> <user> <pass> <dataset>");
            System.exit(1);
        }
        
        ExportViralIsolates evi = new ExportViralIsolates(new File(args[0]), args[1], args[2], args[3]);
        try {
            evi.run();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public ExportViralIsolates(File file, String user, String password, String dataset){
        this.setFile(file);
        this.setDataset(dataset); 
        
        try {
            setLogin(Login.authenticate(user, password));
        } catch (Exception e) {
            e.printStackTrace();
            setLogin(null);
        }
    }
    
    public void run() throws FileNotFoundException{
        FileOutputStream fout = new FileOutputStream(getFile());
        
        ViralIsolateXMLOutputStream xmlout = new ViralIsolateXMLOutputStream(fout);

        PatientExporter<ViralIsolate> ep = new PatientExporter<ViralIsolate>(getLogin(), getDataset(), xmlout);
        ep.run();
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Login getLogin() {
        return login;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getDataset() {
        return dataset;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
