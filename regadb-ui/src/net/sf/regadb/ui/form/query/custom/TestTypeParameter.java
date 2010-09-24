package net.sf.regadb.ui.form.query.custom;

import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.TestTypeComboBox;
import eu.webtoolkit.jwt.WWidget;

public class TestTypeParameter extends BasicParameter {

	private TestTypeComboBox testtypes;
	
	public TestTypeParameter(String description, boolean mandatory, TestTypeComboBox testtypes) {
		super(description, mandatory);

		this.testtypes = testtypes;;
		init();
	}
	
	public TestTypeParameter(IForm form, String description, boolean mandatory){
		this(description,mandatory,new TestTypeComboBox(InteractionState.Adding, form));
	}
	
	protected void init(){
		Transaction t = RegaDBMain.getApp().createTransaction();
		testtypes.fill(t, true);
		t.commit();
	}

	@Override
	public WWidget getWidget() {
		return testtypes;
	}
	
	public TestTypeComboBox getTestTypeComboBox(){
		return testtypes;
	}
	
	public TestType getTestType(){
		return testtypes.currentValue();
	}

	public boolean isValid() {
		return true;
	}
}
