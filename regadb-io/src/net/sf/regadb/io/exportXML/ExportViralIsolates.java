package net.sf.regadb.io.exportXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.export.PatientExporter;
import net.sf.regadb.io.exportXML.ExportToXMLOutputStream.ViralIsolateXMLOutputStream;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;

public class ExportViralIsolates {
    private Login login;
    private String dataset;
    private File file;
    
    public static void main(String args[]){
    	Arguments as = new Arguments();
    	PositionalArgument outputFile = as.addPositionalArgument("output-file", true);
    	PositionalArgument user = as.addPositionalArgument("user", true);
    	PositionalArgument pass = as.addPositionalArgument("pass", true);
    	PositionalArgument dataset = as.addPositionalArgument("dataset", true);
    	
    	if(!as.handle(args))
    		return;
    	
        ExportViralIsolates evi = new ExportViralIsolates(
        		new File(outputFile.getValue()),user.getValue(), pass.getValue(), dataset.getValue());
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
