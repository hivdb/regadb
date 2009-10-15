package net.sf.regadb.io.exportXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.export.PatientExporter;
import net.sf.regadb.io.exportXML.ExportToXMLOutputStream.ViralIsolateXMLOutputStream;
import net.sf.regadb.util.args.Argument;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ExportViralIsolates {
    private Login login;
    private String dataset;
    private File file;
    
    public static void main(String args[]){
    	Arguments as = new Arguments();
    	ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
    	Argument results = as.addArgument("with-results", false);
    	PositionalArgument outputFile = as.addPositionalArgument("output-file", true);
    	PositionalArgument user = as.addPositionalArgument("user", true);
    	PositionalArgument pass = as.addPositionalArgument("pass", true);
    	PositionalArgument dataset = as.addPositionalArgument("dataset", true);
    	
    	if(!as.handle(args))
    		return;
    	
    	if(confDir.isSet())
    		RegaDBSettings.createInstance(confDir.getValue());
    	else
    		RegaDBSettings.createInstance();
    	
        ExportViralIsolates evi = new ExportViralIsolates(
        		new File(outputFile.getValue()),user.getValue(), pass.getValue(), dataset.getValue());
        try {
            evi.run(results.isSet());
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
    
    public void run(boolean exportResults) throws FileNotFoundException{
        FileOutputStream fout = new FileOutputStream(getFile());
        
        ViralIsolateXMLOutputStream xmlout = new ViralIsolateXMLOutputStream(fout, exportResults);

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
