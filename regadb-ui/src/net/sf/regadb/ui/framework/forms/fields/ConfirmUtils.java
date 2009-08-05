package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IConfirmForm;
import net.sf.regadb.ui.framework.forms.IForm;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WInteractWidget;

public class ConfirmUtils 
{
    public static void addConfirmAction(IForm form, WInteractWidget interactWidget)
    {
        if(form instanceof IConfirmForm)
        {
            final IConfirmForm confirmForm = ((IConfirmForm)form);
            interactWidget.enterPressed().addListener(form.getWContainer(), new Signal.Listener()
                    {
                        public void trigger() 
                        {
                            confirmForm.confirmAction();   
                        }
                    });
        }
    }
}
