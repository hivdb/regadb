package net.sf.regadb.ui.framework.widgets.datatable.hibernate;

import net.sf.regadb.ui.framework.widgets.datatable.DateFilter;
import net.sf.regadb.ui.framework.widgets.datatable.ListFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;
import net.sf.witty.wt.i8n.WMessage;

public class HibernateFilterConstraintMapping
{
	public static HibernateFilterConstraint getDateFilterConstraint(DateFilter df, String varName, int filterIndex)
	{
		HibernateFilterConstraint constraint = new HibernateFilterConstraint();
		
		String operator = df.getComboState().key();
			
		if(operator.equals(DateFilter.equals_))
		{
			constraint.clause_ = varName + " = :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, df.getFirstDate()));
		}
		else if(operator.equals(DateFilter.before_))
		{
			constraint.clause_ = varName + " < :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, df.getFirstDate()));
		}
		else if(operator.equals(DateFilter.after_))
		{
			constraint.clause_ = varName + " > :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, df.getFirstDate()));
		}
		else if(operator.equals(DateFilter.between_))
		{
			constraint.clause_ = varName + " between :paramA" + filterIndex + " and :paramB" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("paramA" + filterIndex, df.getFirstDate()));
			constraint.arguments_.add(new Pair<String, Object>("paramB" + filterIndex, df.getSecondDate()));
		}
			
		return constraint;
	}
	
	public static HibernateFilterConstraint getListFilterConstraint(ListFilter lf, String varName, int filterIndex)
	{
		HibernateFilterConstraint constraint = new HibernateFilterConstraint();
		
		WMessage message = lf.getComboValue();
		if(message!=null)
		{
		constraint.clause_ = " " + varName+" = :param" + filterIndex;
		constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, message.value()));
		}
		
		return constraint;
	}
	
	public static HibernateFilterConstraint getStringFilterConstraint(StringFilter sf, String varName, int filterIndex)
	{	
		HibernateFilterConstraint constraint = new HibernateFilterConstraint();
		
		String operator = sf.getComboState().key();
		
		if(operator.equals(StringFilter.beginsWith_))
		{
			constraint.clause_ = varName + " like :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, sf.getStringValue() + "%"));
			//return varName + " like '" + tf_.text() + "%'"; 
		}
		else if(operator.equals(StringFilter.endsWith_))
		{
			constraint.clause_ = varName + " like :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, "%"+sf.getStringValue()));
		}
		else if(operator.equals(StringFilter.contains_))
		{
			constraint.clause_ = varName + " like :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, "%"+sf.getStringValue()+"%"));
		}
		else if(operator.equals(StringFilter.sqlRegExp_))
		{
			constraint.clause_ = varName + " like :param" + filterIndex;
			constraint.arguments_.add(new Pair<String, Object>("param" + filterIndex, sf.getStringValue()));
		}
		
		return constraint;
	}
}
