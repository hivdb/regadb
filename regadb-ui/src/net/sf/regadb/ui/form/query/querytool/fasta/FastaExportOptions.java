package net.sf.regadb.ui.form.query.querytool.fasta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.tools.exportFasta.FastaExporter;
import net.sf.regadb.tools.exportFasta.FastaExporter.FastaId;
import net.sf.regadb.tools.exportFasta.FastaExporter.Mode;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.Filter;
import net.sf.regadb.util.settings.RegaDBSettings;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;

import eu.webtoolkit.jwt.SelectionMode;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WSelectionBox;
import eu.webtoolkit.jwt.WString;

public class FastaExportOptions extends FormTable {
	private QueryToolForm form;
	
	private Label modeL = new Label(tr("form.query.querytool.fastaExport.mode"));
	private ComboBox<FastaExporter.Mode> modeCB;
	private Label outputVarL = new Label(tr("form.query.querytool.fastaExport.outputvar"));
	private ComboBox<OutputVariable> outputVarCB;
	private Label fastaIdL = new Label(tr("form.query.querytool.fastaExport.fastaId"));
	private CheckBox datasetChB;
	private CheckBox patientIdChB;
	private CheckBox sampleIdChB;
	
	private Label orfL = new Label(tr("form.query.querytool.fastaExport.orf"));
	private ComboBox<OpenReadingFrame> orfCB;
	private Label proteinsL = new Label(tr("form.query.querytool.fastaExport.proteins"));
	private WSelectionBox proteinSB;
	private Label symbolL = new Label(tr("form.query.querytool.fastaExport.symbol"));
	private ComboBox<FastaExporter.Symbol> symbolCB;
	private Label alignedL = new Label(tr("form.query.querytool.fastaExport.aligned"));
	private ComboBox<Boolean> alignedCB;
	
	private static final WString alignedYes = tr("form.query.querytool.fastaExport.aligned.yes");
	private static final WString alignedNo = tr("form.query.querytool.fastaExport.aligned.no");
	
	public FastaExportOptions(QueryToolForm form, WContainerWidget parent, QTFastaExporter exporter) {
		super(parent);
		
		this.form = form;
		
		InteractionState is = form.getInteractionState();
		
		init(is);

		addWidgets();
		
		hideWidgets(modeCB.currentValue());
		
		if (exporter != null) {
			if (exporter.getMode() != null)
				select(exporter.getMode().getLocalizedMessage(), modeCB);
			
			if (exporter.getFastaId() != null) {
				if (exporter.getFastaId().contains(FastaId.Dataset))
					datasetChB.setChecked(true);
				if (exporter.getFastaId().contains(FastaId.PatientId))
					patientIdChB.setChecked(true);
				if (exporter.getFastaId().contains(FastaId.SampleId))
					sampleIdChB.setChecked(true);
			}
			
			if (exporter.getOutput() != null) 
				outputVarCB.selectItem(exporter.getOutput().getUniqueName());
			
			if (exporter.getOrf() != null) { 
				orfCB.selectItem(exporter.getOrf());
				loadProteins(exporter.getProteins());
			}
				
			if (exporter.getSymbol() != null)
				select(exporter.getSymbol().getLocalizedMessage(), symbolCB);
			
			if (exporter.isAligned())
				alignedCB.selectItem(alignedYes.toString());
			else 
				alignedCB.selectItem(alignedNo.toString());
			
			hideWidgets(modeCB.currentValue());
		}
	}
	
	public QTFastaExporter getFastaExporter() {
		QTFastaExporter exporter = new QTFastaExporter();
		
		exporter.setMode(modeCB.currentValue());
		
		exporter.setOutput(outputVarCB.currentValue());
		
		EnumSet<FastaId> fastaId = EnumSet.noneOf(FastaId.class);
		if (datasetChB.isChecked())
			fastaId.add(FastaId.Dataset);
		if (patientIdChB.isChecked())
			fastaId.add(FastaId.PatientId);
		if (sampleIdChB.isChecked())
			fastaId.add(FastaId.SampleId);
		exporter.setFastaId(fastaId);
		
		exporter.setOrf(orfCB.currentString());
		Set<String> proteins = new HashSet<String>();
		for (int i : proteinSB.getSelectedIndexes()) 
			proteins.add(proteinSB.getItemText(i).toString());
		exporter.setProteins(proteins);
		
		exporter.setSymbol(symbolCB.currentValue());
		
		exporter.setAligned(alignedCB.currentValue());
		
		return exporter;
	}
	
	private void select(String i18nName, ComboBox combo) {
		combo.selectItem(tr(i18nName).toString());
	}
	
	private void init(InteractionState is) {
		modeCB = new ComboBox<FastaExporter.Mode>(is, null);
		modeCB.addNoSelectionItem();
		for (FastaExporter.Mode m : FastaExporter.Mode.values()) {
			modeCB.addItem(new DataComboMessage<Mode>(m, tr(m.getLocalizedMessage()).toString()));
		}
		modeCB.addComboChangeListener(new Signal.Listener() {
			public void trigger() {
				hideWidgets(modeCB.currentValue());
			}
		});
		
		outputVarCB = new ComboBox<OutputVariable>(is, null);
		updateOutputVars();
		
		datasetChB = new CheckBox(is, null, tr("form.query.querytool.fastaExport.id.dataset"));
		datasetChB.setChecked(true);
		patientIdChB = new CheckBox(is, null, tr("form.query.querytool.fastaExport.id.patientId"));
		patientIdChB.setChecked(true);
		sampleIdChB = new CheckBox(is, null, tr("form.query.querytool.fastaExport.id.sampleId"));
		sampleIdChB.setChecked(true);
		
		orfCB = new ComboBox<OpenReadingFrame>(is, null);
		Transaction tr = RegaDBMain.getApp().createTransaction();

		Filter filter = RegaDBSettings.getInstance().getInstituteConfig().getOrganismFilter();
		for (OpenReadingFrame orf : tr.getOpenReadingFrames()) {
			if (filter == null || filter.compareRegexp(orf.getGenome().getOrganismName())) 
				orfCB.addItem(new DataComboMessage<OpenReadingFrame>(orf, orf.getGenome().getOrganismName() + " - " + orf.getName()));
		}
		
		orfCB.addComboChangeListener(new Signal.Listener(){
			public void trigger() {
				loadProteins(null);
			}
		});
		
		orfCB.selectIndex(0);
		proteinSB = new WSelectionBox();
		proteinSB.setSelectionMode(SelectionMode.ExtendedSelection);
		proteinSB.setEnabled(is == InteractionState.Editing || is == InteractionState.Adding);
		loadProteins(null);
		
		symbolCB = new ComboBox<FastaExporter.Symbol>(is, null);
		for (FastaExporter.Symbol s : FastaExporter.Symbol.values()) {
			symbolCB.addItem(new DataComboMessage<FastaExporter.Symbol>(s, tr(s.getLocalizedMessage()).toString()));
		}
		
		alignedCB = new ComboBox<Boolean>(is, null);
		alignedCB.addItem(new DataComboMessage<Boolean>(true, alignedYes.toString()));
		alignedCB.addItem(new DataComboMessage<Boolean>(false, alignedNo.toString()));
	}
	
	private void addWidgets() {
		addLineToTable(modeL, modeCB);
		
		addLineToTable(outputVarL, outputVarCB);
		
		WContainerWidget fastaId = new WContainerWidget();
		fastaId.setInline(true);
		fastaId.addWidget(datasetChB);
		fastaId.addWidget(patientIdChB);
		fastaId.addWidget(sampleIdChB);
		addLineToTable(fastaIdL, fastaId);
		
		addLineToTable(orfL, orfCB);
		addLineToTable(proteinsL, proteinSB);
		addLineToTable(symbolL, symbolCB);
		addLineToTable(alignedL, alignedCB);
	}
	
	private void loadProteins(Set<String> selectedProteins) {
		proteinSB.clear();
		
		Set<Integer> selectedIndices = new HashSet<Integer>();
		int index = 0;
		List<Protein> proteins = new ArrayList<Protein>(orfCB.currentValue().getProteins());
		Collections.sort(proteins, new Comparator<Protein>(){
			public int compare(Protein p1, Protein p2) {
				return (p1.getStartPosition() < p2.getStartPosition() ? -1 : (p1.getStartPosition() == p2.getStartPosition() ? 0 : 1));
			}
		});
		for (Protein p : proteins) {
			proteinSB.addItem(p.getAbbreviation());
			if (selectedProteins !=null && selectedProteins.contains(p.getAbbreviation()))
				selectedIndices.add(index);
			++index;
		}
		
		proteinSB.setSelectedIndexes(selectedIndices);
	}
	
	private void hideWidgets(FastaExporter.Mode mode) {
		hideRow(outputVarL, mode == null);
		hideRow(fastaIdL, mode == null);
		
		hideRow(orfL, mode != FastaExporter.Mode.BaseOnProteins);
		hideRow(proteinsL, mode != FastaExporter.Mode.BaseOnProteins);
		hideRow(symbolL, mode != FastaExporter.Mode.BaseOnProteins);
		hideRow(alignedL, mode != FastaExporter.Mode.BaseOnProteins);
	}
	
	private void hideRow(Label label, boolean hidden) {
		for (int i = 0; i < getRowCount(); i++)
			if (getElementAt(i, 0).getChildren().contains(label))
				getRowAt(i).setHidden(hidden);
	}
	
	public void updateOutputVars() {
		OutputVariable currentOV = outputVarCB.currentValue();
		
		outputVarCB.clearItems();
		for (OutputVariable ov : form.getEditorModel().getQueryEditor().getRootClause().getExportedOutputVariables())
			if (ov.getObject().getTableName().equals("ViralIsolate")) 
				outputVarCB.addItem(new DataComboMessage<OutputVariable>(ov, ov.getUniqueName()));
		
		if (currentOV != null)
			outputVarCB.selectItem(currentOV.getUniqueName());
	}
}
