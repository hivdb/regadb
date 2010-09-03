package net.sf.regadb.ui.form.query.custom;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public abstract class CustomQuery extends FormWidget{
	private String name, description;
	private List<Parameter> parameters = new ArrayList<Parameter>();

	private WPushButton run;
	private WAnchor download;
	private WText error;
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public CustomQuery(WString formName) {
		this(formName, InteractionState.Viewing);
	}

	public CustomQuery(WString formName, InteractionState interactionState) {
		super(formName, interactionState);
		
		init();
		doInit();
	}
	
	protected abstract void init();
	
	protected void doInit(){
		WContainerWidget gDescr = new WContainerWidget(this);
//		gDescr.setTitle(getName());
		gDescr.addWidget(new WText(getDescription(),TextFormat.XHTMLText));
		
		FormTable tParams = new FormTable(gDescr);
		for(Parameter p : parameters){
			tParams.addLineToTable(new WLabel(p.getDescription()), p.getWidget());
		}
		
		run = new WPushButton(WString.tr("form.query.custom.run"));
		run.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg0) {
				doRun();
			};
		});
		
		download = new WAnchor();
		error = new WText();
		
		addWidget(run);
		addWidget(new WText(" "));
		addWidget(download);
		addWidget(error);
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WString deleteObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void redirectAfterDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveData() {
		// TODO Auto-generated method stub
		
	}

	public List<Parameter> getParameters(){
		return parameters;
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
		this.description = description;
	}
	
	public File getResultDir(){
        return RegaDBSettings.getInstance().getInstituteConfig().getQueryResultDir();
    }
	
	private void doRun(){
		run.disable();
		
		try{
			File result = run();
			
			if(result != null){
				showDownload(result);
			}else{
				showError("No results.");
			}
		}
		catch(Exception e){
			e.printStackTrace();

			showError(e.getMessage());
		}
		
		run.enable();
	}
	
	private void showDownload(File result){
		error.hide();
		download.show();
		
		download.setText("Download Result [" + df.format(new Date()) + "]");
        WFileResource res = new WFileResource(getMimeType(), result.getAbsolutePath());
        res.suggestFileName(getFileName());
        download.setResource(res);
	}
	private void showError(String msg){
		download.hide();
		error.show();
		
		error.setText(msg);
	}
	
	public String getFileName(){
		return getName().toLowerCase().replace(' ', '_') +".csv";
	}
	public String getMimeType(){
		return "application/csv";
	}
	
	public abstract File run() throws Exception;
}
