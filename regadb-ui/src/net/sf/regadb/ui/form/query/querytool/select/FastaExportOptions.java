package net.sf.regadb.ui.form.query.querytool.select;

import java.util.List;

import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.tools.exportFasta.FastaExporter;
import net.sf.regadb.tools.exportFasta.FastaExporter.Mode;
import net.sf.regadb.ui.form.query.querytool.QueryToolForm;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;

import com.pharmadm.custom.rega.queryeditor.OutputVariable;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WContainerWidget;

public class FastaExportOptions extends FormTable {
	private Label modeL = new Label(tr("form.query.querytool.fastaExport.mode"));
	private ComboBox<FastaExporter.Mode> modeCB;
	private Label outputVarL = new Label(tr("form.query.querytool.fastaExport.outputvar"));
	private ComboBox<OutputVariable> outputVarCB;
	private Label fastaIdL = new Label(tr("form.query.querytool.fastaExport.fastaId"));
	private CheckBox datasetChB;
	private CheckBox patientIdChB;
	private CheckBox sampleIdChB;
	
	private Label regionL = new Label(tr("form.query.querytool.fastaExport.region"));
	private ComboBox<OpenReadingFrame> orfCB;
	private ComboBox<Protein> proteinCB;
	private Label symbolL = new Label(tr("form.query.querytool.fastaExport.symbol"));
	private ComboBox<FastaExporter.Symbol> symbolCB;
	private Label alignedL = new Label(tr("form.query.querytool.fastaExport.aligned"));
	private ComboBox<Boolean> alignedCB;
	
	public FastaExportOptions(QueryToolForm form, WContainerWidget parent, QTFastaExporter exporter) {
		super(parent);
		
		InteractionState is = form.getInteractionState();
		
		modeCB = new ComboBox<FastaExporter.Mode>(is, null);
		modeCB.addNoSelectionItem();
		for (FastaExporter.Mode m : FastaExporter.Mode.values()) {
			modeCB.addItem(new DataComboMessage<Mode>(m, tr(m.getLocalizedMessage()).toString()));
		}
		
		outputVarCB = new ComboBox<OutputVariable>(is, null);
		
		datasetChB = new CheckBox(is, null);
		patientIdChB = new CheckBox(is, null);
		sampleIdChB = new CheckBox(is, null);
		
		orfCB = new ComboBox<OpenReadingFrame>(is, null);
		Transaction tr = RegaDBMain.getApp().createTransaction();
		for (OpenReadingFrame orf : tr.getOpenReadingFrames()) 
			if (RegaDBSettings.getInstance().getInstituteConfig().getOrganismFilter().compareRegexp(orf.getGenome().getOrganismName())) 
				orfCB.addItem(new DataComboMessage<OpenReadingFrame>(orf, orf.getGenome().getOrganismName() + " - " + orf.getName()));
		orfCB.addComboChangeListener(new Signal.Listener(){
			public void trigger() {
				loadProteins();
			}
		});
		proteinCB = new ComboBox<Protein>(is, null);
		loadProteins();
		
		symbolCB = new ComboBox<FastaExporter.Symbol>(is, null);
		for (FastaExporter.Symbol s : FastaExporter.Symbol.values()) {
			symbolCB.addItem(new DataComboMessage<FastaExporter.Symbol>(s, tr(s.getLocalizedMessage()).toString()));
		}
		
		alignedCB = new ComboBox<Boolean>(is, null);
		alignedCB.addItem(new DataComboMessage<Boolean>(true, tr("form.query.querytool.fastaExport.aligned.yes").toString()));
		alignedCB.addItem(new DataComboMessage<Boolean>(false, tr("form.query.querytool.fastaExport.aligned.no").toString()));
	}
	
	private void loadProteins() {
		proteinCB.clearItems();
		
		for (Protein p : orfCB.currentValue().getProteins()) 
			proteinCB.addItem(new DataComboMessage<Protein>(p, p.getAbbreviation()));
	}
	
	private void addWidgets(FastaExporter.Mode mode) {
		this.clear();
	}
	
	public void updateOutputVars(List<OutputVariable> outputVars) {
		//TODO
	}
}
