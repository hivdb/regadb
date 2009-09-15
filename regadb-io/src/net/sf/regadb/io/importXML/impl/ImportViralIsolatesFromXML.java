package net.sf.regadb.io.importXML.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportViralIsolatesFromXML 
{
    public static void main(String[] args) throws SAXException, IOException, WrongUidException, WrongPasswordException, DisabledUserException 
    {
    	Arguments as = new Arguments();
    	PositionalArgument xml	= as.addPositionalArgument("processed-viral-isolates.xml",true);
    	PositionalArgument user	= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass	= as.addPositionalArgument("regadb password", true);
    	PositionalArgument ds	= as.addPositionalArgument("dataset", false);
    	ValueArgument conf		= as.addValueArgument("conf-dir", "configuration directory", false);
    	
    	if(!as.handle(args))
    		return;
    	
    	if(conf.isSet())
    		RegaDBSettings.createInstance(conf.getValue());
    	else
    		RegaDBSettings.createInstance();
    	
        ImportXML instance = new ImportXML(user.getValue(), pass.getValue());
        instance.importViralIsolates(new InputSource(new FileReader(new File(xml.getValue()))), (ds.isSet() ? ds.getValue() : null));
        instance.login.closeSession();
    }
}
