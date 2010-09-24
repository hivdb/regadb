package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WValidator;

public class StringFilter extends WContainerWidget implements IFilter 
{
	private FilterOperatorCombo combo_;
	private FilterTF tf_;
	
	public final static String beginsWith_ = "dataTable.filter.stringFilter.beginsWith";
	public final static String endsWith_ = "dataTable.filter.stringFilter.endsWith";
	public final static String contains_ = "dataTable.filter.stringFilter.contains";
	public final static String sqlRegExp_ = "dataTable.filter.stringFilter.sqlRegExp";
	
	public StringFilter()
	{
		tf_ = new FilterTF(null);
		tf_.setInline(false);
		combo_ = new FilterOperatorCombo(tf_);
		combo_.setInline(false);
		
		addWidget(combo_);
		addWidget(tf_);
		
		//filling of the combo-box with operators		
		combo_.addItem(tr(beginsWith_));
		combo_.addItem(tr(endsWith_));
		combo_.addItem(tr(contains_));
		combo_.addItem(tr(sqlRegExp_));
	}
	
	public WContainerWidget getFilterWidget()
	{
		return this;
	}
	
	public WString getComboState()
	{
		return combo_.getCurrentText();
	}
	
	public String getStringValue()
	{
		return tf_.getText().trim();
	}

	public HibernateFilterConstraint getConstraint(String varName, int filterIndex) {
		HibernateFilterConstraint constraint = new HibernateFilterConstraint();
		
		String operator = getComboState().getKey();
		String param = "param"+filterIndex;
		
		if(operator.equals(StringFilter.beginsWith_))
		{
			constraint.clause_ = "lower(" + varName + ") like :"+ param;
			constraint.arguments_.add(new Pair<String, Object>(param, getStringValue().toLowerCase() + "%"));
			//return varName + " like '" + tf_.text() + "%'"; 
		}
		else if(operator.equals(StringFilter.endsWith_))
		{
			constraint.clause_ = "lower(" + varName + ") like :"+ param;
			constraint.arguments_.add(new Pair<String, Object>(param, "%"+getStringValue().toLowerCase()));
		}
		else if(operator.equals(StringFilter.contains_))
		{
			constraint.clause_ = "lower(" + varName + ") like :"+ param;
			constraint.arguments_.add(new Pair<String, Object>(param, "%"+getStringValue().toLowerCase()+"%"));
		}
		else if(operator.equals(StringFilter.sqlRegExp_))
		{
			constraint.clause_ = varName + " like :"+ param;
			constraint.arguments_.add(new Pair<String, Object>(param, getStringValue()));
		}
		
		return constraint;
	}

	public boolean isValid() {
		return tf_.validate() == WValidator.State.Valid;
	}
}
