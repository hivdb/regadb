package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WFileUpload;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WWidget;

public class FileUpload extends WContainerWidget implements IFormField{
	private WAnchor link;
	private WFileUpload uploadFile;
	private WPushButton uploadButton;
	
	private boolean mandatory;
	
	public FileUpload(InteractionState istate, IForm form) {
        link = new WAnchor("dummy", lt(""), this);
        link.setStyleClass("link");
        
        uploadFile = new WFileUpload(this);
        uploadFile.uploaded.addListener(this, new Signal.Listener()  {
            public void trigger() {
                link.setHidden(uploadFile.clientFileName()==null);
                uploadButton.setEnabled(true);
                uploadButton.setText(tr("form.general.button.upload"));
                setAnchor(lt(uploadFile.clientFileName()), uploadFile.spoolFileName());
            }
        });
        
        uploadButton = new WPushButton(tr("form.general.button.upload"), this);
        uploadButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
            public void trigger(WMouseEvent a) {
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
	
	public void setAnchor(WString title, String url) {
	        link.setText(title);
	        link.setRef(url);
	}

	public void flagErroneous() {
		setStyleClass("Wt-invalid");
	}

	public void flagValid() {
		setStyleClass("");
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

	public void setConfirmAction(Signal.Listener se) {
        if(getFormWidget()!=null) {
            getFormWidget().enterPressed.removeAllListeners();
            if(se != null)
                getFormWidget().enterPressed.addListener(this, se);
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
