package net.sf.regadb.io.importXML.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.io.importXML.ImportFromXMLBase.Keep;
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
    	ValueArgument keepOld	= as.addValueArgument("keep-old", "TestResult,PatientAttributeValue,...", false);
    	ValueArgument keepBoth	= as.addValueArgument("keep-both", "TestResult,PatientAttributeValue,...", false);
    	ValueArgument keepNew	= as.addValueArgument("keep-new", "TestResult,PatientAttributeValue,...", false);
    	ValueArgument keepDefault = as.addValueArgument("keep-default", "NEW|old|both", false);
    	ValueArgument conf		= as.addValueArgument("conf-dir", "configuration directory", false);
    	
    	if(!as.handle(args))
    		return;
    	
        if(conf.isSet())
        	RegaDBSettings.createInstance(conf.getValue());
        else
        	RegaDBSettings.createInstance();
        
        Map<String, Keep> keepMap = new HashMap<String, Keep>();
        if(keepBoth.isSet())
        	add(keepMap, Keep.BOTH, keepBoth.getValue());
        if(keepOld.isSet())
        	add(keepMap, Keep.OLD, keepOld.getValue());
        if(keepNew.isSet())
        	add(keepMap, Keep.NEW, keepNew.getValue());

        
        ImportXML instance = new ImportXML(user.getValue(), pass.getValue());
        instance.setKeepMap(keepMap);
        
        if(keepDefault.isSet())
        	instance.setDefaultKeep(Enum.valueOf(Keep.class, keepDefault.getValue().toUpperCase()));
        
        instance.importPatients(new InputSource(new FileReader(new File(xml.getValue()))), ds.getValue());
        instance.login.closeSession();
    }
    
    private static void add(Map<String,Keep> keepMap, Keep keep, String classNames){
    	for(String className : classNames.split(","))
    		keepMap.put(className, keep);
    }
}
