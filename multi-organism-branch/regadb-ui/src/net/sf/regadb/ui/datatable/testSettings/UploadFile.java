package net.sf.regadb.ui.datatable.testSettings;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WFileUpload;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WText;

public class UploadFile extends WContainerWidget 
{
    public WFileUpload upload_;
    private WPushButton uploadButton_;
    public WText uploadedFile_;
    
    public UploadFile()
    {
        super();
        upload_ = new WFileUpload(this);
        uploadButton_ = new WPushButton(tr("analysis.uploadField.upload"), this);
        uploadButton_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        uploadButton_.setText(tr("analysis.uploadField.uploading"));
                        uploadButton_.setEnabled(false);
                        upload_.upload();
                    }
                });
        upload_.uploaded.addListener(new SignalListener<WEmptyEvent>()
                {
                   public void notify(WEmptyEvent a) 
                   {
                	   if(upload_.clientFileName()!=null)
                	   {
	                       clear();
	                       uploadedFile_ = new WText(lt("Uploaded " + upload_.clientFileName()), UploadFile.this);
                	   }
                	   else
                	   {
                           uploadButton_.setText(tr("analysis.uploadField.upload"));
                           uploadButton_.setEnabled(true);
                	   }
                   }
                });
    }
}
