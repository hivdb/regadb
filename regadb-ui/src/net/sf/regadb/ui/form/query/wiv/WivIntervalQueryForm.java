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
        
        super.addParameter("var_start_date", tr("form.query.wiv.label.startDate"), startDate);
        super.addParameter("var_end_date", tr("form.query.wiv.label.endDate"), endDate);
    }
    
    public Date getStartDate(){
        return startDate.getDate();
    }
    
    public Date getEndDate(){
        return endDate.getDate();
    }

}
