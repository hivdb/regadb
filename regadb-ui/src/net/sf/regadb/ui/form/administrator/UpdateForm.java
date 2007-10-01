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
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.ImportDrugs;
import net.sf.regadb.io.importXML.ImportException;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportHandler;
import net.sf.regadb.io.importXML.ImportFromXMLBase.SyncMode;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WFontWeight;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WTextFormatting;
import net.sf.witty.wt.i8n.WMessage;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UpdateForm extends FormWidget
{
    private WText progressText_ = new WText(tr("form.update_central_server.running"));
    private WImage warningImage_ = new WImage("pics/formWarning.gif");
    
    private WGroupBox testGroup_ = new WGroupBox(tr("form.update_central_server.test"));
    private WText testText_ = new WText();
    
    private WGroupBox attributesGroup_ = new WGroupBox(tr("form.update_central_server.attribute"));
    private WText attributesText_ = new WText();
    
    private WGroupBox drugsGroup_ = new WGroupBox(tr("form.update_central_server.drug"));
    private WText drugClassTitle_ = new WText(tr("form.admin.update_central_server.drugClass.title"));
    private WText drugClassText_ = new WText();
    
    private WText drugGenericsTitle_ = new WText(tr("form.admin.update_central_server.drugGenerics.title"));
    private WText drugGenericsText_ = new WText();
    
    private WText drugCommercialsTitle_ = new WText(tr("form.admin.update_central_server.drugCommercials.title"));
    private WText drugCommercialsText_ = new WText();

    
    public UpdateForm(WMessage formName, InteractionState interactionState)
    {
        super(formName, interactionState);
        init();
    }
    
    public void init()
    {
        if(getInteractionState()==InteractionState.Editing)
        {
            addWidget(warningImage_);
            addWidget(progressText_);
        }
        
        addWidget(testGroup_);
        addWidget(attributesGroup_);
        addWidget(drugsGroup_);
        
        testGroup_.addWidget(testText_);
        
        attributesGroup_.addWidget(attributesText_);
        
        drugsGroup_.addWidget(drugClassTitle_);
        drugsGroup_.addWidget(new WBreak());
        drugsGroup_.addWidget(drugClassText_);
        
        drugsGroup_.addWidget(drugGenericsTitle_);
        drugsGroup_.addWidget(new WBreak());
        drugsGroup_.addWidget(drugGenericsText_);
        
        drugsGroup_.addWidget(drugCommercialsTitle_);
        drugsGroup_.addWidget(new WBreak());
        drugsGroup_.addWidget(drugCommercialsText_);
        
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
            handleAttributes(true);
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
        testText_.setFormatting(WTextFormatting.PlainFormatting);
        testText_.setText(lt(imp.getLog().toString()));
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
        attributesText_.setFormatting(WTextFormatting.PlainFormatting);
        attributesText_.setText(lt(imp.getLog().toString()));
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
        handleFields(drugClassTitle_, drugClassText_, report);
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
        handleFields(drugGenericsTitle_, drugGenericsText_, report);
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
        handleFields(drugCommercialsTitle_, drugCommercialsText_, report);
        drugCommercials.delete();
        t.commit();
    }
    
    private void showInstalledItems()
    {
        Transaction t;
        
        //Tests
        t = RegaDBMain.getApp().createTransaction();
        List<Test> tests = t.getTests();
        ArrayList<String> testDescriptions = new ArrayList<String>();
        for(Test test : tests)
        {
            testDescriptions.add(test.getDescription()+ " - " + test.getTestType().getDescription());
        }
        handleFields(null, testText_, testDescriptions);
        t.commit();
        
        //Attributes
        t = RegaDBMain.getApp().createTransaction();
        List<Attribute> attributes = t.getAttributes();
        ArrayList<String> attributesNames = new ArrayList<String>();
        for(Attribute attribute : attributes)
        {
            attributesNames.add(attribute.getName()+ " - " + attribute.getAttributeGroup().getGroupName());
        }
        handleFields(null, attributesText_, attributesNames);
        t.commit();
        
        //Drug Class
        t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> classDrugs = t.getClassDrugs();
        ArrayList<String> classDrugsNames = new ArrayList<String>();
        for(DrugClass dc : classDrugs)
        {
            classDrugsNames.add(dc.getClassName());
        }
        handleFields(drugClassTitle_, drugClassText_, classDrugsNames);
        t.commit();
        
        //Generic Drugs
        t = RegaDBMain.getApp().createTransaction();
        List<DrugGeneric> genericDrugs = t.getGenericDrugs();
        ArrayList<String> genericDrugsNames = new ArrayList<String>();
        for(DrugGeneric dg : genericDrugs)
        {
            genericDrugsNames.add(dg.getGenericName());
        }
        handleFields(drugGenericsTitle_, drugGenericsText_, genericDrugsNames);
        t.commit();
        
        //Commercial Drugs
        t = RegaDBMain.getApp().createTransaction();
        List<DrugCommercial> commercialDrugs = t.getCommercialDrugs();
        ArrayList<String> commercialDrugsNames = new ArrayList<String>();
        for(DrugCommercial dc : commercialDrugs)
        {
            commercialDrugsNames.add(dc.getName());
        }
        handleFields(drugCommercialsTitle_, drugCommercialsText_, commercialDrugsNames);
        t.commit();
    }
    
    private void handleFields(WText title, WText text, ArrayList<String> report)
    {
        String field = "";
        for(String line : report)
        {
            field += line + "<br>";
        }
        
        if(title!=null)
        {
            title.decorationStyle().font().setWeight(WFontWeight.Bold);
        }
        text.setText(lt(field));
        
        report.clear();
    }
    
    @Override
    public void saveData()
    {   
        handleAttributes(false);
        handleTests(false);
        handleDrugs(false);
        
        RegaDBMain.getApp().getTree().getTreeContent().updateFromCentralServerUpdateView.selectNode();
    }
    
    @Override
    public void cancel()
    {
        
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
}
