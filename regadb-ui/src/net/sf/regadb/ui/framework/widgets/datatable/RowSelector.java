package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.ui.framework.RegaDBMain;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/*
 * Default implementation of IRowSelector
 * */
public class RowSelector<DataType> implements IRowSelector<DataType>
{
    public void selectAction(DataType selectedElement) throws NullPointerException
    {
        Criteria c = RegaDBMain.getApp().createCriteria(selectedElement.getClass());
        c.add(Restrictions.idEq(selectedElement));
        Object o = c.uniqueResult();
        if(o!=null)
        {
            DataType dt = (DataType)o;
        }
        else
        {
            throw new NullPointerException();
        }
    }
}
