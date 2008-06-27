package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IConfirmForm;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WInteractWidget;

public class ConfirmUtils 
{
    public static void addConfirmAction(IForm form, WInteractWidget interactWidget)
    {
        if(form instanceof IConfirmForm)
        {
            final IConfirmForm confirmForm = ((IConfirmForm)form);
            interactWidget.enterPressed.addListener(new SignalListener<WEmptyEvent>()
                    {
                        public void notify(WEmptyEvent a) 
                        {
                            confirmForm.confirmAction();   
                        }
                    });
        }
    }
}
