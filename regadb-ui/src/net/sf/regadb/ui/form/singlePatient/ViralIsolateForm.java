package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WMenu;
import net.sf.witty.wt.widgets.WMenuOrientation;
import net.sf.witty.wt.widgets.WStackedWidget;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WText;

public class ViralIsolateForm extends FormWidget
{
	private ViralIsolate viralIsolate_;

	private WMenu tabForm_;

	public ViralIsolateForm(InteractionState interactionState, WMessage formName, ViralIsolate viralIsolate)
	{
		super(formName, interactionState);
		viralIsolate_ = viralIsolate;

		init();
	}

	public void init()
	{
		WTable layout = new WTable(this);
		layout.resize(new WLength(100, WLengthUnit.Percentage), new WLength());

		WStackedWidget menuContents = new WStackedWidget(layout.elementAt(1, 0));
		tabForm_ = new WMenu(menuContents, WMenuOrientation.Horizontal, layout.elementAt(0, 0));
		// tabForm_.setStyleClass("menu");
		tabForm_.resize(new WLength(100, WLengthUnit.Percentage), new WLength());
		layout.elementAt(1, 0).resize(new WLength(20, WLengthUnit.FontEx), new WLength());

		tabForm_.addItem(tr("form.viralIsolate.editView.tab.viralIsolate"), new ViralIsolateMainForm(this));
		tabForm_.addItem(tr("form.viralIsolate.editView.tab.proteins"), new WText(lt("lala")));
	}

	@Override
	public void saveData()
	{
		// TODO Auto-generated method stub

	}
}
