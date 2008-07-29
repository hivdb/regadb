package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

public class FileUpload extends WContainerWidget implements IFormField{
	private WAnchor link;
	private WFileUpload uploadFile;
	private WPushButton uploadButton;
	
	private boolean mandatory;
	
	public FileUpload(InteractionState istate, IForm form) {
        link = new WAnchor("dummy", lt(""), this);
        link.setStyleClass("link");
        
        uploadFile = new WFileUpload(this);
        uploadFile.uploaded.addListener(new SignalListener<WEmptyEvent>()  {
            public void notify(WEmptyEvent a) {
                link.setHidden(uploadFile.clientFileName()==null);
                uploadButton.setEnabled(true);
                uploadButton.setText(tr("form.general.button.upload"));
                setAnchor(lt(uploadFile.clientFileName()), uploadFile.spoolFileName());
            }
        });
        
        uploadButton = new WPushButton(tr("form.general.button.upload"), this);
        uploadButton.clicked.addListener(new SignalListener<WMouseEvent>() {
            public void notify(WMouseEvent a) {
                uploadButton.setText(tr("form.general.button.uploading"));
            	uploadFile.upload();
            }
        });
        
        if (istate == InteractionState.Viewing || istate == InteractionState.Deleting) {
        	uploadFile.setHidden(true);
        	uploadButton.setHidden(true);
        }
        
		form.addFormField(this);
	}
	
	public WFileUpload getFileUpload() {
		return uploadFile;
	}
	
	public void setAnchor(WMessage title, String url) {
	        link.label().setText(title);
	        link.setRef(url);
	}

	public void flagErroneous() {
		this.setStyleClass("form-field textfield edit-invalid");
	}

	public void flagValid() {
		this.setStyleClass("form-field textfield edit-valid");
	}

	public String getFormText() {
		return getFileUpload().clientFileName();
	}

	public WFormWidget getFormWidget() {
		return uploadButton;
	}

	public WWidget getViewWidget() {
		return link;
	}

	public WWidget getWidget() {
		return this;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setConfirmAction(SignalListener<WEmptyEvent> se) {
        if(getFormWidget()!=null) {
            getFormWidget().enterPressed.removeAllListeners();
            if(se != null)
                getFormWidget().enterPressed.addListener(se);
            }
	}

	public void setFormText(String text) {
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean validate() {
		if (isMandatory()) {
			return getFileUpload().clientFileName() != null;
		}
		return true;
	}
}
