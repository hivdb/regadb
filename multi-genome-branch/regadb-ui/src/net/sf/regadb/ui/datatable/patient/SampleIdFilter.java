package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

public class SampleIdFilter extends StringFilter {
	@Override
	public HibernateFilterConstraint getConstraint(String varName, int filterIndex) {
		HibernateFilterConstraint hfc = super.getConstraint(varName, filterIndex);
		if(hfc.clause_!=null)
			hfc.clause_ = " patient.id in (select vi.patient.id from ViralIsolate vi where " + hfc.clause_ + ") ";
		return hfc;
	}
}
