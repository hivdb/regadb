package net.sf.regadb.ui.datatable.testSettings;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import eu.webtoolkit.jwt.Signal;

public class UploadFile extends FileUpload 
{
    
    public UploadFile(InteractionState istate, IForm form)
    {
        super(istate, form);
        getFileUpload().uploaded().addListener(this, new Signal.Listener() {
                   public void trigger() {
                	   if(getFileUpload().clientFileName()!=null) {
	                       setAnchor(lt(getFileUpload().clientFileName()), getFileUpload().spoolFileName());
                	   }
                   }
                });
    }
}
