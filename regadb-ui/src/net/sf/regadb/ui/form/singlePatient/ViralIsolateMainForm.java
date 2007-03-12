package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.event.WEmptyEvent;
import net.sf.witty.wt.widgets.event.WMouseEvent;

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
	private ComboBox seqComboBox;
	private WPushButton addButton;
	private WPushButton deleteButton;

	// NtSeq group
	private WGroupBox ntSeqGroup_;
	private WTable ntSeqGroupTable_;
	private Label seqLabel;
	private TextField seqLabelTF;
	private Label seqDateL;
	private DateField seqDateTF;
	private Label subTypeL;
	private TextField subTypeTF;
    
    private static final String defaultSequenceLabel_ = "Sequence ";

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
		seqComboBox = new ComboBox(InteractionState.Editing, null);
        sequenceGroupTable_.putElementAt(0, 0, seqComboBox);        

		// NtSeq group
		sequenceGroupTable_.elementAt(1, 0).setColumnSpan(2);
		ntSeqGroup_ = new WGroupBox(tr("form.viralIsolate.editView.ntSeq"), sequenceGroupTable_.elementAt(1, 0));
		ntSeqGroupTable_ = new WTable(ntSeqGroup_);
		seqLabel = new Label(tr("form.viralIsolate.editView.seqLabel"));
		seqLabelTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
        seqLabelTF.setMandatory(true);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, seqLabel, seqLabelTF);
		seqDateL = new Label(tr("form.viralIsolate.editView.seqDate"));
		seqDateTF = new DateField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, seqDateL, seqDateTF);
		subTypeL = new Label(tr("form.viralIsolate.editView.subType"));
		subTypeTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(ntSeqGroupTable_, subTypeL, subTypeTF);
        
        addButtons();
	}
	
	void fillData(ViralIsolate vi)
	{
		sampleDateTF.setDate(vi.getSampleDate());
		sampleIdTF.setText(vi.getSampleId());
        
        for(NtSequence ntseq : vi.getNtSequences())
        {
            if(ntseq.getLabel()==null || ntseq.getLabel().equals(""))
                {
                    ntseq.setLabel(getUniqueSequenceLabel(vi));
                }
            seqComboBox.addItem(new DataComboMessage<NtSequence>(ntseq, ntseq.getLabel()));
        }

        
        seqComboBox.addComboChangeListener(new SignalListener<WEmptyEvent>()
                {
                    public void notify(WEmptyEvent a)
                    {
                        setSequenceData(((DataComboMessage<NtSequence>)seqComboBox.currentText()).getValue());
                    }
                });
        
        addButton.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        addSeqData();
                    }
                });
            
    }
    
    private void addSeqData()
    {
        String label = getUniqueSequenceLabel(viralIsolateForm_.getViralIsolate());
        NtSequence newSeq = new NtSequence(viralIsolateForm_.getViralIsolate(), "nucleotides", label , null, null);
        viralIsolateForm_.getViralIsolate().getNtSequences().add(newSeq);
        
        DataComboMessage<NtSequence> msg = new DataComboMessage<NtSequence>(newSeq, label);
        seqComboBox.addItem(msg);
        setSequenceData(newSeq);
        
        seqComboBox.selectItem(msg);
    }
    
    private void setSequenceData(NtSequence seq)
    {
        seqLabelTF.setText(seq.getLabel());
        seqDateTF.setDate(seq.getSequenceDate());
    }
    
    private String getUniqueSequenceLabel(ViralIsolate vi)
    {
        int largestLabel = 0;
        List<Integer> labelNumbers = new ArrayList<Integer>();
        int labelNumber = 0;
        
        for(NtSequence ntseq : vi.getNtSequences())
        {
            String label = ntseq.getLabel();

            if(label!=null)
            {
                if(label.startsWith(defaultSequenceLabel_));
                {
                    label = label.substring(label.lastIndexOf(" ")+1, ntseq.getLabel().length());
                    try
                    {
                        labelNumber = Integer.parseInt(label);
                        labelNumbers.add(labelNumber);
                    }
                    catch(NumberFormatException e)
                    {
                        //do nothing if it isn't a parsable number
                    }
                }
            }
        }
        Collections.sort(labelNumbers);
        largestLabel++;
        
        
        return defaultSequenceLabel_ + (labelNumbers.size()!=0?(labelNumbers.get(labelNumbers.size()-1) + 1):1);
    }
    
    private void addButtons()
    {
        sequenceGroupTable_.elementAt(0, 1).setContentAlignment(WHorizontalAlignment.AlignRight);
        WTable buttonTable = new WTable(sequenceGroupTable_.elementAt(0, 1));
        sequenceGroupTable_.elementAt(0, 1).setInline(true);
        addButton = new WPushButton(tr("form.viralIsolate.addButton"), buttonTable.elementAt(0, 0));
        deleteButton = new WPushButton(tr("form.viralIsolate.deleteButton"), buttonTable.elementAt(0, 1));
        if(viralIsolateForm_.getInteractionState()!=InteractionState.Editing)
        {
            addButton.disable();
            deleteButton.disable();
        }
        else
        {
            addButton.enable();
            deleteButton.enable();
        }
    }
}
