package net.sf.regadb.ui.framework.forms.administrator;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.ImportDrugs;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WFontWeight;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WText;

public class UpdateForm extends FormWidget
{
    private WText progressText_ = new WText(tr("form.update_central_server.running"));
    private WImage warningImage_ = new WImage("pics/formWarning.gif");
    
    private WGroupBox testGroup_ = new WGroupBox(tr("form.update_central_server.test"));
    private WGroupBox attributesGroup_ = new WGroupBox(tr("form.update_central_server.attribute"));
    
    private WGroupBox drugsGroup_ = new WGroupBox(tr("form.update_central_server.drug"));
    private WText drugClassTitle_ = new WText(tr("form.admin.update_central_server.drugClass.title"));
    private WText drugClassText_ = new WText();
    
    private WText drugGenericsTitle_ = new WText(tr("form.admin.update_central_server.drugGenerics.title"));
    private WText drugGenericsText_ = new WText();
    
    private WText drugCommercialsTitle_ = new WText(tr("form.admin.update_central_server.drugCommercials.title"));
    private WText drugCommercialsText_ = new WText();

    
    public UpdateForm(InteractionState interactionState)
    {
        super(tr("form.update_central_server"), interactionState);
        init();
    }
    
    public void init()
    {
        addWidget(warningImage_);
        addWidget(progressText_);
        
        addWidget(testGroup_);
        addWidget(attributesGroup_);
        addWidget(drugsGroup_);
        
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
        handleDrugs(true);
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
        report = ImportDrugs.importDrugClasses(t, drugClasses, simulate);
        handleDrug(drugClassTitle_, drugClassText_, report);
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
        report = ImportDrugs.importGenericDrugs(t, drugGenerics, simulate);
        handleDrug(drugGenericsTitle_, drugGenericsText_, report);
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
        report = ImportDrugs.importCommercialDrugs(t, drugCommercials, simulate);
        handleDrug(drugCommercialsTitle_, drugCommercialsText_, report);
        drugCommercials.delete();
        t.commit();
    }
    
    private void handleDrug(WText title, WText text, ArrayList<String> report)
    {
        String field = "";
        for(String line : report)
        {
            field += line + "<br>";
        }
        
        title.decorationStyle().font().setWeight(WFontWeight.Bold);
        text.setText(lt(field));
        
        report.clear();
    }
    
    @Override
    public void saveData()
    {        
        handleDrugs(false);
    }
    
    @Override
    public void cancel()
    {
        
    }
    
    @Override
    public void deleteObject()
    {
        
    }

    @Override
    public void redirectAfterDelete() 
    {
        
    }
}
