package net.sf.regadb.io.importXML;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.SplicingPosition;
import net.sf.regadb.db.Transaction;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ImportGenomes {
    private Transaction transaction=null;
    private boolean simulate=true;
    
    private StringBuilder log = new StringBuilder();
    
    public StringBuilder getLog(){ return log; }
    
    public ImportGenomes(){
        
    }
    
    public ImportGenomes(Transaction transaction, boolean simulate){
        this.setTransaction(transaction);
        this.setSimulate(simulate);
    }
    
    public Collection<Genome> importFromXml(File xmlFile){
        return importFromXml(xmlFile,false);
    }
    public Collection<Genome> importFromXml(File xmlFile, boolean newObjects){
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(xmlFile.getAbsolutePath());
            return importFromXml(doc, newObjects);
            
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Collection<Genome> importFromXml(Document doc, boolean newObjects){
        List<Genome> genomes = new ArrayList<Genome>();
        
        Element root = doc.getRootElement();
        for(Object el : root.getChildren("genome"))
            genomes.add(toGenome((Element)el, newObjects));
        
        return genomes;
    }

    public Genome toGenome(Element el, boolean newObject){
        
        String organismName = el.getAttributeValue("organismName");
        Genome g = getGenome(organismName);
        newObject = g == null; 
        
        if(!isSimulate()){
            if(newObject){
                g = new Genome();
                g.setOrganismName(organismName);
            }
            
            g.setOrganismDescription(el.getAttributeValue("organismDescription"));
            g.setGenbankNumber(el.getAttributeValue("genbankNumber"));
            g.setVersion(Integer.parseInt(el.getAttributeValue("version")));
        }
        logSync("genome "+ organismName, newObject);
        
        for(Object child : el.getChildren("openReadingFrame"))
            toOpenReadingFrame(g, (Element)child, newObject);
        
        if(!isSimulate() && getTransaction() != null)
            getTransaction().save(g);
            
        return g;
    }
    
    public OpenReadingFrame toOpenReadingFrame(Genome g, Element el, boolean newObject){
        String name = el.getAttributeValue("name");
        OpenReadingFrame orf = null;
        
        if(!newObject)
            orf = getOpenReadingFrame(g, name);

        newObject = orf == null;
        if(!isSimulate()){
            if(newObject){
                orf = new OpenReadingFrame();
                orf.setName(name);
                
                g.getOpenReadingFrames().add(orf);
                orf.setGenome(g);
            }
                
            orf.setDescription(el.getAttributeValue("description"));
            orf.setReferenceSequence(el.getAttributeValue("referenceSequence"));
            orf.setVersion(Integer.parseInt(el.getAttributeValue("version")));
        }
        logSync("open reading frame "+ name, newObject);
        
        for(Object child : el.getChildren("protein"))
            toProtein(orf, (Element)child, newObject);
        
        return orf;
    }
    
    public Protein toProtein(OpenReadingFrame orf, Element el, boolean newObject){
        String abbreviation = el.getAttributeValue("abbreviation");
        Protein p = null;
        
        if(!newObject)
            p = getProtein(orf, abbreviation);
        
        newObject = p == null;
        if(!isSimulate()){
            if(newObject){
                p = new Protein();
                p.setAbbreviation(el.getAttributeValue("abbreviation"));
                
                orf.getProteins().add(p);
                p.setOpenReadingFrame(orf);
            }
        
            p.setFullName(el.getAttributeValue("fullName"));
            p.setStartPosition(Integer.parseInt(el.getAttributeValue("startPosition")));
            p.setStopPosition(Integer.parseInt(el.getAttributeValue("stopPosition")));
            p.setVersion(Integer.parseInt(el.getAttributeValue("version")));
        }
        logSync("protein "+ abbreviation, newObject);
        
        for(Object child : el.getChildren("splicingPosition"))
            toSplicingPosition(p, (Element)child, newObject);
        
        return p;
    }
    
    public SplicingPosition toSplicingPosition(Protein p, Element el, boolean newObject){
        int position = Integer.parseInt(el.getAttributeValue("position"));
        SplicingPosition sp = null;
        
        if(!newObject)
            sp = getSplicingPosition(p, position);
        
        newObject = sp == null;
        if(!isSimulate()){
            if(newObject){
                sp = new SplicingPosition();
                sp.setPosition(position);
                
                p.getSplicingPositions().add(sp);
                sp.setProtein(p);
            }
        
            sp.setVersion(Integer.parseInt(el.getAttributeValue("version")));
        }
        logSync("splicing position "+ position, newObject);
        
        return sp;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setSimulate(boolean simulate) {
        this.simulate = simulate;
    }

    public boolean isSimulate() {
        return simulate;
    }
    
    public void logSync(String s, boolean newObject){
        if(newObject)
            if(isSimulate())
                log.append("Adding ");
            else
                log.append("Added ");
        else
            if(isSimulate())
                log.append("Synchronizing ");
            else
                log.append("Synchronized ");
        log.append(s +"\n");
    }
    
    protected Genome getGenome(String organismName){
        if(getTransaction() != null)
            return getTransaction().getGenome(organismName);
        else
            return null;
        
    }
    protected OpenReadingFrame getOpenReadingFrame(Genome genome, String name){
        if(getTransaction() != null)
            return getTransaction().getOpenReadingFrame(genome, name);
        else
            return null;
        
    }
    protected Protein getProtein(OpenReadingFrame orf, String abbreviation){
        if(getTransaction() != null)
            return getTransaction().getProtein(orf, abbreviation);
        else
            return null;
        
    }
    protected SplicingPosition getSplicingPosition(Protein p, int position){
        if(getTransaction() != null)
            return getTransaction().getSplicingPosition(p, position);
        else
            return null;
    }
}
