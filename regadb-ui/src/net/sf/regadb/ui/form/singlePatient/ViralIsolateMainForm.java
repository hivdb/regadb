package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.widgets.WComboBox;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;

public class ViralIsolateMainForm extends WContainerWidget
{
	private ViralIsolateForm viralIsolateForm_;

	// General group
	private WGroupBox generalGroup_;
	private WTable generalGroupTable_;
	private Label sampleDateL;
	private DateField sampleDateTF;
	private Label sampleIdL;
	private TextField sampleIdTF;
	private Label sampleTypeL;
	private TextField sampleTypeTF;

	// Sequence group
	private WGroupBox sequenceGroup_;
	private WTable sequenceGroupTable_;
	private WComboBox seqComboBox;
	private WPushButton addButton;
	private WPushButton deleteButton;

	// NtSeq group
	private WGroupBox ntSeqGroup_;
	private WTable ntSeqGroupTable_;
	private Label seqIdL;
	private TextField seqIdTF;
	private Label seqDateL;
	private DateField seqDateTF;
	private Label subTypeL;
	private TextField subTypeTF;

	public ViralIsolateMainForm(ViralIsolateForm viralIsolateForm)
	{
		super();
		viralIsolateForm_ = viralIsolateForm;

		init();
	}

	public void init()
	{
		// General group
		generalGroup_ = new WGroupBox(tr("form.viralIsolate.editView.general"), this);
		generalGroupTable_ = new WTable(generalGroup_);
		sampleDateL = new Label(tr("form.viralIsolate.editView.sampleDate"));
		sampleDateTF = new DateField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(generalGroupTable_, sampleDateL, sampleDateTF);
		sampleIdL = new Label(tr("form.viralIsolate.editView.sampleId"));
		sampleIdTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(generalGroupTable_, sampleIdL, sampleIdTF);
		sampleTypeL = new Label(tr("form.viralIsolate.editView.sampleType"));
		sampleTypeTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(generalGroupTable_, sampleTypeL, sampleTypeTF);

		// Sequence group
		sequenceGroup_ = new WGroupBox(tr("form.viralIsolate.editView.sequence"), this);
		sequenceGroupTable_ = new WTable(sequenceGroup_);
		// comboBox
		seqComboBox = new WComboBox(sequenceGroupTable_.elementAt(0, 0));
		// buttons
		sequenceGroupTable_.elementAt(0, 1).setContentAlignment(WHorizontalAlignment.AlignRight);
		WTable buttonTable = new WTable(sequenceGroupTable_.elementAt(0, 1));
		sequenceGroupTable_.elementAt(0, 1).setInline(true);
		addButton = new WPushButton(tr("form.viralIsolate.addButton"), buttonTable.elementAt(0, 0));
		deleteButton = new WPushButton(tr("form.viralIsolate.deleteButton"), buttonTable.elementAt(0, 1));

		// NtSeq group
		sequenceGroupTable_.elementAt(1, 0).setColumnSpan(2);
		ntSeqGroup_ = new WGroupBox(tr("form.viralIsolate.editView.ntSeq"), sequenceGroupTable_.elementAt(1, 0));
		ntSeqGroupTable_ = new WTable(ntSeqGroup_);
		seqIdL = new Label(tr("form.viralIsolate.editView.seqId"));
		seqIdTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, seqIdL, seqIdTF);
		seqDateL = new Label(tr("form.viralIsolate.editView.seqDate"));
		seqDateTF = new DateField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, seqDateL, seqDateTF);
		subTypeL = new Label(tr("form.viralIsolate.editView.subType"));
		subTypeTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, subTypeL, subTypeTF);

	}
}
