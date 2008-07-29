package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;

public class UploadFile extends FileUpload 
{
    
    public UploadFile(InteractionState istate, IForm form)
    {
        super(istate, form);
        getFileUpload().uploaded.addListener(new SignalListener<WEmptyEvent>() {
                   public void notify(WEmptyEvent a) {
                	   if(getFileUpload().clientFileName()!=null) {
	                       setAnchor(lt(getFileUpload().clientFileName()), getFileUpload().spoolFileName());
                	   }
                   }
                });
    }
}
