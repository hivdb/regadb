package net.sf.regadb.io.importXML.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.util.args.Argument;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.args.Arguments.ArgumentException;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportPatientsFromXML 
{
    public static void main(String[] args) throws SAXException, IOException, WrongUidException, WrongPasswordException, DisabledUserException, ArgumentException 
    {
    	Arguments as = new Arguments();
    	PositionalArgument xml	= as.addPositionalArgument("patients.xml",true);
    	PositionalArgument user	= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass	= as.addPositionalArgument("regadb password", true);
    	PositionalArgument ds	= as.addPositionalArgument("dataset", false);

    	ValueArgument doAdd		= as.addValueArgument("do-add", "TestResult,PatientAttributeValue,...", false);
    	ValueArgument dontAdd	= as.addValueArgument("dont-add", "TestResult,PatientAttributeValue,...", false);
    	Argument defaultDontAdd = as.addArgument("default-dont-add", false); 

    	ValueArgument doDelete		= as.addValueArgument("do-delete", "TestResult,PatientAttributeValue,...", false);
    	ValueArgument dontDelete	= as.addValueArgument("dont-delete", "TestResult,PatientAttributeValue,...", false);
    	Argument defaultDontDelete	= as.addArgument("default-dont-delete", false); 

    	ValueArgument doUpdate		= as.addValueArgument("do-update", "TestResult,PatientAttributeValue,...", false);
    	ValueArgument dontUpdate	= as.addValueArgument("dont-update", "TestResult,PatientAttributeValue,...", false);
    	Argument defaultDontUpdate	= as.addArgument("default-dont-update", false); 

    	ValueArgument conf		= as.addValueArgument("conf-dir", "configuration directory", false);
    	
    	if(!as.handle(args))
    		return;
    	
        if(conf.isSet())
        	RegaDBSettings.createInstance(conf.getValue());
        else
        	RegaDBSettings.createInstance();
        
        ImportXML instance = new ImportXML(user.getValue(), pass.getValue());
        
        if(doAdd.isSet())
        	add(instance.getDoAddMap(),doAdd.getValue(),true);
        if(dontAdd.isSet())
        	add(instance.getDoAddMap(),dontAdd.getValue(),false);
        instance.setDefaultDoAdd(!defaultDontAdd.isSet());

        if(doDelete.isSet())
        	add(instance.getDoDeleteMap(),doDelete.getValue(),true);
        if(dontDelete.isSet())
        	add(instance.getDoDeleteMap(),dontDelete.getValue(),false);
        instance.setDefaultDoDelete(!defaultDontDelete.isSet());

        if(doUpdate.isSet())
        	add(instance.getDoUpdateMap(),doUpdate.getValue(),true);
        if(dontUpdate.isSet())
        	add(instance.getDoUpdateMap(),dontUpdate.getValue(),false);
        instance.setDefaultDoUpdate(!defaultDontUpdate.isSet());

        instance.importPatients(new InputSource(new FileReader(new File(xml.getValue()))), ds.getValue());
        instance.login.closeSession();
    }
    
    private static void add(Map<String,Boolean> doMap, String classNames, boolean doit){
    	for(String className : classNames.split(","))
    		doMap.put(className, doit);
    }
}
