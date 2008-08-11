package net.sf.regadb.ui.form.administrator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.SplicingPosition;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.ImportDrugs;
import net.sf.regadb.io.importXML.ImportException;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.io.importXML.ImportHandler;
import net.sf.regadb.io.importXML.ImportFromXMLBase.SyncMode;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage;
import net.sf.regadb.ui.framework.widgets.warning.WarningMessage.MessageType;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTextFormatting;
import net.sf.witty.wt.i8n.WMessage;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UpdateForm extends FormWidget
{
    private WMessage progressText_ = tr("form.update_central_server.running");
    private WImage warningImage_ = new WImage("pics/formWarning.gif");
    
    private WContainerWidget genomeGroup_ = new WGroupBox(tr("form.update_central_server.genome"));
    private WContainerWidget testGroup_ = new WGroupBox(tr("form.update_central_server.test"));
    private WContainerWidget attributesGroup_ = new WGroupBox(tr("form.update_central_server.attribute"));
    private WContainerWidget eventsGroup_ = new WGroupBox(tr("form.update_central_server.event"));
    private WContainerWidget drugsGroup_ = new WGroupBox(tr("form.update_central_server.drug"));

    private WMessage drugClassTitle_ = tr("form.admin.update_central_server.drugClass.title");
    private WMessage drugGenericsTitle_ = tr("form.admin.update_central_server.drugGenerics.title");
    private WMessage drugCommercialsTitle_ = tr("form.admin.update_central_server.drugCommercials.title");

    
    public UpdateForm(WMessage formName, InteractionState interactionState)
    {
        super(formName, interactionState);
        init();
    }
    
    public void init()
    {
        if(getInteractionState()==InteractionState.Editing)
        {
            addWidget(new WarningMessage(warningImage_, progressText_, MessageType.INFO));
        }
        
        addWidget(genomeGroup_);
        addWidget(testGroup_);
        addWidget(attributesGroup_);
        addWidget(eventsGroup_);
        addWidget(drugsGroup_);
        
        fillData();
        
        addControlButtons();
    }
    
    public void fillData()
    {
        if(getInteractionState()==InteractionState.Viewing)
        {
            showInstalledItems();
        }
        else
        {
            handleGenomes(true);
            handleAttributes(true);
            handleEvents(true);
            handleTests(true);
            handleDrugs(true);
        }
    }
    
    private void handleTests(final boolean simulate)
    {
        FileProvider fp = new FileProvider();
        File testsFile = RegaDBMain.getApp().createTempFile("tests", "xml");
        try 
        {
            fp.getFile("regadb-tests", "tests.xml", testsFile);
        }
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        final ImportFromXML imp = new ImportFromXML();
        try 
        {
            final Transaction t = RegaDBMain.getApp().createTransaction();
            imp.loadDatabaseObjects(t);
            imp.readTests(new InputSource(new FileReader(testsFile)), new ImportHandler<Test>()
                    {
                        public void importObject(Test object) 
                        {
                            try 
                            {
                                imp.sync(t, object, SyncMode.Update, simulate);
                            } 
                            catch (ImportException e) 
                            {
                                e.printStackTrace();
                            }
                        }
                    });
            t.commit();
        } 
        catch (SAXException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        testsFile.delete();
        new WLogText(testGroup_, lt(imp.getLog().toString()));
    }
    
    private void handleAttributes(final boolean simulate)
    {
        FileProvider fp = new FileProvider();
        File attributesFile = RegaDBMain.getApp().createTempFile("attributes", "xml");
        try 
        {
            fp.getFile("regadb-attributes", "attributes.xml", attributesFile);
        }
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        final ImportFromXML imp = new ImportFromXML();
        try 
        {
            final Transaction t = RegaDBMain.getApp().createTransaction();
            imp.loadDatabaseObjects(t);
            imp.readAttributes(new InputSource(new FileReader(attributesFile)), new ImportHandler<Attribute>()
                    {
                        public void importObject(Attribute object) 
                        {
                            try 
                            {
                                imp.sync(t, object, SyncMode.Update, simulate);
                            } 
                            catch (ImportException e) 
                            {
                                e.printStackTrace();
                            }
                        }
                    });
            t.commit();
        } 
        catch (SAXException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        attributesFile.delete();
        new WLogText(attributesGroup_, lt(imp.getLog().toString()));
    }
    
    private void handleEvents(final boolean simulate)
    {
        FileProvider fp = new FileProvider();
        File eventsFile = RegaDBMain.getApp().createTempFile("events", "xml");
        try 
        {
            fp.getFile("regadb-events", "events.xml", eventsFile);
        }
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        final ImportFromXML imp = new ImportFromXML();
        try 
        {
            final Transaction t = RegaDBMain.getApp().createTransaction();
            imp.loadDatabaseObjects(t);
            imp.readEvents(new InputSource(new FileReader(eventsFile)), new ImportHandler<Event>()
                    {
                        public void importObject(Event object) 
                        {
                            try 
                            {
                                imp.sync(t, object, SyncMode.Update, simulate);
                            } 
                            catch (ImportException e) 
                            {
                                e.printStackTrace();
                            }
                        }
                    });
            t.commit();
        } 
        catch (SAXException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        eventsFile.delete();
        new WLogText(eventsGroup_, lt(imp.getLog().toString()));
    }
    
    private void handleDrugs(boolean simulate)
    {
        Transaction t;
        ArrayList<String> report;
        
        FileProvider fp = new FileProvider();
        
        File drugClasses = RegaDBMain.getApp().createTempFile("DrugClasses", "xml");
        try 
        {
            fp.getFile("regadb-drugs", "DrugClasses.xml", drugClasses);
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        t = RegaDBMain.getApp().createTransaction();
        report = ImportDrugs.importDrugClasses(new DrugTransaction(t), drugClasses, simulate);
        handleFields(drugsGroup_, drugClassTitle_, report);
        drugClasses.delete();
        t.commit();

        File drugGenerics = RegaDBMain.getApp().createTempFile("DrugGenerics", "xml");
        try 
        {
            fp.getFile("regadb-drugs", "DrugGenerics.xml", drugGenerics);
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        t = RegaDBMain.getApp().createTransaction();
        report = ImportDrugs.importGenericDrugs(new DrugTransaction(t), drugGenerics, simulate);
        handleFields(drugsGroup_, drugGenericsTitle_, report);
        drugGenerics.delete();
        t.commit();
        
        File drugCommercials = RegaDBMain.getApp().createTempFile("DrugCommercials", "xml");
        try 
        {
            fp.getFile("regadb-drugs", "DrugCommercials.xml", drugCommercials);
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        t = RegaDBMain.getApp().createTransaction();
        report = ImportDrugs.importCommercialDrugs(new DrugTransaction(t), drugCommercials, simulate);
        handleFields(drugsGroup_, drugCommercialsTitle_, report);
        drugCommercials.delete();
        t.commit();
    }
    
    private void handleGenomes(boolean simulate){
        Transaction t;
        
        FileProvider fp = new FileProvider();
        
        File genomesXml = RegaDBMain.getApp().createTempFile("genomes", "xml");
        try 
        {
            fp.getFile("regadb-genomes", "genomes.xml", genomesXml);
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        t = RegaDBMain.getApp().createTransaction();
        
        ImportGenomes ig = new ImportGenomes(t,simulate);
        ig.importFromXml(genomesXml);
                
        new WLogText(genomeGroup_, lt(ig.getLog().toString()));
        
        t.commit();
    }

    private void showInstalledItems()
    {
        Transaction t;
        
        //Genomes
        t = RegaDBMain.getApp().createTransaction();
        List<Genome> genomes = t.getGenomes();
        
        for(Genome g : genomes)
        {
            List<List<String>> genomeDescriptions = new ArrayList<List<String>>();
            List<String> genomeTitles = new ArrayList<String>();
            genomeTitles.add("organism");
            genomeTitles.add("description");
            
            ArrayList<String> row = new ArrayList<String>();
            row.add(g.getOrganismName());
            row.add(g.getOrganismDescription());
            genomeDescriptions.add(row);
            
            handleFields(genomeGroup_, genomeTitles, genomeDescriptions);

            List<List<String>> proteinDescriptions = new ArrayList<List<String>>();
            List<String> proteinTitles = new ArrayList<String>();
            proteinTitles.add("open reading frame");
            proteinTitles.add("protein");
            proteinTitles.add("full name");
            proteinTitles.add("start position");
            proteinTitles.add("stop position");
            proteinTitles.add("splicing positions");            

            for(OpenReadingFrame orf : g.getOpenReadingFrames()){
                for(Protein p : orf.getProteins()){
                    
                    row = new ArrayList<String>();
                    row.add(orf.getName());
                    row.add(p.getAbbreviation());
                    row.add(p.getFullName());
                    row.add(""+p.getStartPosition());
                    row.add(""+p.getStopPosition());
                    
                    String splicings = "";
                    for(SplicingPosition sp : p.getSplicingPositions())
                        splicings += sp.getPosition()+" ";
                    row.add(splicings);
                    
                    proteinDescriptions.add(row);
                }
            }
            handleFields(genomeGroup_, proteinTitles, proteinDescriptions);
        }
        
        t.commit();
        
        //Tests
        t = RegaDBMain.getApp().createTransaction();
        List<Test> tests = t.getTests();
        List<List<String>> testDescriptions = new ArrayList<List<String>>();
        for(Test test : tests)
        {
            ArrayList<String> row = new ArrayList<String>();
            row.add(test.getDescription());
            row.add(test.getTestType().getDescription());
            row.add(test.getTestType().getGenome() == null ? "": test.getTestType().getGenome().getOrganismName());
            testDescriptions.add(row);
        }
        List<String> testTitles = new ArrayList<String>();
        testTitles.add("test");
        testTitles.add("test type");
        testTitles.add("organism");
        
        handleFields(testGroup_, testTitles, testDescriptions);
        t.commit();
        
        //Attributes
        t = RegaDBMain.getApp().createTransaction();
        List<Attribute> attributes = t.getAttributes();
        
        List<List<String>> attributeDescriptions = new ArrayList<List<String>>();
        for(Attribute attribute : attributes)
        {
            ArrayList<String> row = new ArrayList<String>();
            row.add(attribute.getName());
            row.add(attribute.getAttributeGroup().getGroupName());
            attributeDescriptions.add(row);
        }
        List<String> attributeTitles = new ArrayList<String>();
        attributeTitles.add("attribute");
        attributeTitles.add("attribute group");
        
        handleFields(attributesGroup_, attributeTitles, attributeDescriptions);
        t.commit();
        
        //Events
        t = RegaDBMain.getApp().createTransaction();
        List<Event> events = t.getEvents();
        List<String> eventTitles = new ArrayList<String>();
        eventTitles.add("event");
        
        List<List<String>> eventDescriptions = new ArrayList<List<String>>();
        for(Event event : events)
        {
            List<String> row = new ArrayList<String>();
            row.add(event.getName());
            eventDescriptions.add(row);
        }
        handleFields(eventsGroup_, eventTitles, eventDescriptions);
        t.commit();
        
        //Drug Class
        t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> classDrugs = t.getClassDrugs();
        
        List<String> classTitles = new ArrayList<String>();
        classTitles.add("drug class");
        
        List<List<String>> classDescriptions = new ArrayList<List<String>>();
        for(DrugClass dc : classDrugs)
        {
            List<String> row = new ArrayList<String>();
            row.add(dc.getClassName());
            classDescriptions.add(row);
        }
        handleFields(drugsGroup_, classTitles, classDescriptions);
        t.commit();        

        
        //Generic Drugs
        t = RegaDBMain.getApp().createTransaction();
        List<DrugGeneric> genericDrugs = t.getGenericDrugs();
        List<String> genericTitles = new ArrayList<String>();
        genericTitles.add("generic drug");
        
        List<List<String>> genericDescriptions = new ArrayList<List<String>>();
        for(DrugGeneric dg : genericDrugs)
        {
            List<String> row = new ArrayList<String>();
            row.add(dg.getGenericName());
            genericDescriptions.add(row);
        }
        handleFields(drugsGroup_, genericTitles, genericDescriptions);
        t.commit();
        
        //Commercial Drugs
        t = RegaDBMain.getApp().createTransaction();
        List<DrugCommercial> commercialDrugs = t.getCommercialDrugs();
        List<String> commercialTitles = new ArrayList<String>();
        commercialTitles.add("commercial drug");
        
        List<List<String>> commercialDescriptions = new ArrayList<List<String>>();
        for(DrugCommercial dc : commercialDrugs)
        {
            List<String> row = new ArrayList<String>();
            row.add(dc.getName());
            commercialDescriptions.add(row);
        }
        handleFields(drugsGroup_, commercialTitles, commercialDescriptions);
        t.commit();
    }
    
    private void handleFields(WContainerWidget parent, List<String> titles, List<List<String>> data)
    {
        WMessage[] messages = new WMessage[titles.size()];
        for (int i = 0 ; i < titles.size() ; i++) {
            messages[i] = lt(titles.get(i));
        }
        
        SimpleTable table = new SimpleTable(parent);
        table.setHeaders(messages);
        table.distributeWidths();
        
        for (List<String> dataRow : data) {
            WText[] txt = new WText[dataRow.size()];
            for (int i = 0 ; i < dataRow.size() ; i++) {
                txt[i] = new WText(lt(dataRow.get(i)));
            }           
            table.addRow(txt);
        }
    }
    
    private void handleFields(WContainerWidget parent, WMessage title, ArrayList<String> report) {
        WGroupBox group = new WGroupBox(title, parent);
        
        String field = "";
        for(String line : report) {
            field += line + "\n";
        }
        new WLogText(group, lt(field));
        
        
        report.clear();     
    }
    
    @Override
    public void saveData()
    {   
        handleGenomes(false);
        handleAttributes(false);
        handleEvents(false);
        handleTests(false);
        handleDrugs(false);
        
        RegaDBMain.getApp().getTree().getTreeContent().updateFromCentralServerUpdateView.selectNode();
    }
    
    @Override
    public void cancel()
    {
        redirectToView(RegaDBMain.getApp().getTree().getTreeContent().administratorMain, RegaDBMain.getApp().getTree().getTreeContent().updateFromCentralServerUpdateView);
    }
    
    @Override
    public WMessage deleteObject()
    {
        return null;
    }

    @Override
    public void redirectAfterDelete() 
    {
        
    }
    
    private class WLogText extends WText {
        public WLogText(WContainerWidget parent, WMessage msg) {
            super(parent);
            setFormatting(WTextFormatting.PlainFormatting);
            setText(msg);
            setStyleClass("log-area");
        }
    }
}
