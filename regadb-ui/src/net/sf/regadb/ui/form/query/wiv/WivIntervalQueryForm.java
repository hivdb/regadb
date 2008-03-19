package net.sf.regadb.ui.form.query.wiv;

import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.witty.wt.i8n.WMessage;

public abstract class WivIntervalQueryForm extends WivQueryForm{
    
    public WivIntervalQueryForm(WMessage formName, WMessage description, WMessage filename){
        super(formName, description, filename);
    }
    
    public void init(){
        super.init();
        
        super.addParameter("var_start_date", tr("form.query.wiv.label.startDate"), new DateField(InteractionState.Editing,this));
        super.addParameter("var_end_date", tr("form.query.wiv.label.endDate"), new DateField(InteractionState.Editing,this));
    }

}
