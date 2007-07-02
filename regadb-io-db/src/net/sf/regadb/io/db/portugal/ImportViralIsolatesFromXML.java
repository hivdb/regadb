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
    if(args.length<3)
    {
        System.err.println("Provide a ViralIsolate xml input file as input parameter, a user and a password");
    }
    else
    {
        ImportXML instance = new ImportXML(args[1], args[2]);
        instance.importViralIsolates(new InputSource(new FileReader(new File(args[0]))));
    }
    
    }
}
