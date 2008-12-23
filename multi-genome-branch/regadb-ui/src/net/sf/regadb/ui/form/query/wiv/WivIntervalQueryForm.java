package net.sf.regadb.ui.form.query.wiv;

import java.util.Date;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public abstract class WivIntervalQueryForm extends WivQueryForm{
    private DateField startDate, endDate;
    
    public WivIntervalQueryForm(WString formName, WString description, WString filename){
        super(formName, description, filename);
    }
    
    public void init(){
        super.init();
        
        startDate = new DateField(InteractionState.Editing,this, RegaDBSettings.getInstance().getDateFormat());
        endDate = new DateField(InteractionState.Editing,this, RegaDBSettings.getInstance().getDateFormat());
        
        endDate.setDate(new Date());
        
        super.addParameter("var_start_date", tr("form.query.wiv.label.startDate"), startDate);
        super.addParameter("var_end_date", tr("form.query.wiv.label.endDate"), endDate);
    }
    
    public Date getStartDate(){
        return startDate.getDate();
    }
    
    public void setStartDate(Date d){
        startDate.setDate(d);
    }
    
    public Date getEndDate(){
        return endDate.getDate();
    }
    
    public void setEndDate(Date d){
        endDate.setDate(d);
    }
}
