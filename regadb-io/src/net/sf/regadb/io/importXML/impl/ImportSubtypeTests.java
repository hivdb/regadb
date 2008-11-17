package net.sf.regadb.io.importXML.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportSubtypeTests {
    public static void main(String[] args) throws SAXException, IOException, WrongUidException, WrongPasswordException, DisabledUserException 
    {
        if(args.length<4)
        {
            System.err.println("Provide a ViralIsolate xml input file (with *only* subtype tests) as input parameter, a user, a password and a dataset (optional)");
        }
        else
        {
            ImportXML instance = new ImportXML(args[1], args[2]);
            instance.importSubtypeTests(new InputSource(new FileReader(new File(args[0]))), (args.length > 3 ? args[3] : null));
            instance.login.closeSession();
        }
    }
}