package net.sf.regadb.io.importXML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class ResistanceInterpretationParser extends DefaultHandler
{
    enum ParseState{score};
    
    ParseState state_ = null;
    
    String value_;
    
    String drug_;
    Integer level_;
    double gss_;
    String description_;
    Character sir_;
    ArrayList<String > mutations_ = new ArrayList<String>();
    String remarks_;
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
    {
        if(qName.toLowerCase().equals("score"))
        {
            state_ = ParseState.score;
            drug_ = null;
            level_ = null;
            description_ = null;
            sir_ = null;
            mutations_.clear();
            remarks_ = null;
        }
        
        if(state_ == ParseState.score)
        {
            value_ = null;
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException 
    {
        if(state_ == ParseState.score)
        {
            if(qName.toLowerCase().equals("drug"))
            {
                drug_ = value_;
            }
            else if(qName.toLowerCase().equals("gss"))
            {
                gss_ = Double.parseDouble(value_);
            }
            else if(qName.toLowerCase().equals("level"))
            {
                level_ = Integer.parseInt(value_);
            }
            else if(qName.toLowerCase().equals("description"))
            {
                description_ = value_;
            }
            else if(qName.toLowerCase().equals("sir"))
            {
                sir_ = value_.charAt(0);
            }
            else if(qName.toLowerCase().equals("mutations"))
            {
                if(value_!=null)
                {
                    StringTokenizer tok = new StringTokenizer(value_, " ");
                    while(tok.hasMoreTokens())
                    {
                        mutations_.add(tok.nextToken());
                    }
                }
            }
            else if(qName.toLowerCase().equals("remarks"))
            {
                remarks_ = value_;
            }
            
            if(qName.toLowerCase().equals("score"))
            {
                state_ = null;
                completeScore(drug_, level_, gss_, description_, sir_, mutations_, remarks_);
            }
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException 
    {
        if (value_ == null)
            value_ = new String(ch, start, length);
        else
            value_ += new String(ch, start, length);
    }

    public abstract void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks);
    
    public void parse(InputSource source)  throws SAXException, IOException 
    {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(this);
        xmlReader.setErrorHandler(this);
        xmlReader.parse(source);
    }
}