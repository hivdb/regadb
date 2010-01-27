package net.sf.regadb.io.importXML;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.xml.sax.SAXException;


public abstract class ResistanceDiscordanceInterpretationParser extends ResistanceInterpretationParser {

	private int rule_;

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
                    String token;
                    while(tok.hasMoreTokens()) {
                        token = tok.nextToken();
                        mutations_.add(token);
                    }
                }
            }
            else if(qName.toLowerCase().equals("rule"))
            {
            	rule_ = Integer.parseInt(value_);
            }
            else if(qName.toLowerCase().equals("remarks"))
            {
                remarks_ = value_;
            }
            
            if(qName.toLowerCase().equals("score"))
            {
                state_ = null;
                completeScore(drug_, level_, gss_, description_, sir_, mutations_, rule_, remarks_);
            }
        }
    }

	abstract public void completeScore(String drug, Integer level, double gss,
			String description, Character sir, ArrayList<String> mutations,
			int rule, String remarks);

	@Override
	public void completeScore(String drug, int level, double gss,
			String description, char sir, ArrayList<String> mutations,
			String remarks) { throw new UnsupportedOperationException(); }
	
}
