package net.sf.regadb.io.db.portugal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportViralIsolatesFromXML 
{
    public static void main(String[] args) throws SAXException, IOException, WrongUidException, WrongPasswordException, DisabledUserException 
    {
    ImportXML instance = new ImportXML();
    
    if(args.length<1)
    {
        System.err.println("Provide a ViralIsolate xml input file as input parameter");
    }
    
    instance.importViralIsolates(new InputSource(new FileReader(new File(args[0]))));
    }
}
