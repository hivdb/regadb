package net.sf.regadb.ui.form.query.wiv;

import java.util.Date;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.witty.wt.i8n.WMessage;

public abstract class WivIntervalQueryForm extends WivQueryForm{
    private DateField startDate, endDate;
    
    public WivIntervalQueryForm(WMessage formName, WMessage description, WMessage filename){
        super(formName, description, filename);
    }
    
    public void init(){
        super.init();
        
        startDate = new DateField(InteractionState.Editing,this);
        endDate = new DateField(InteractionState.Editing,this);
        
        endDate.setDate(new Date());
        
        super.addParameter("var_start_date", tr("query.wiv.parameter.after"), startDate);
        super.addParameter("var_end_date", tr("query.wiv.parameter.before"), endDate);
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
