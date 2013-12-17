package net.sf.regadb.ui.form.query.custom.ghb_hcv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.form.query.custom.CustomQuery;
import net.sf.regadb.ui.form.query.custom.DatasetParameter;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.util.datatable.DataTable;
import net.sf.regadb.util.datatable.XlsDataTable;
import net.sf.regadb.util.settings.AttributeItem;
import net.sf.regadb.util.settings.EventItem;
import net.sf.regadb.util.settings.GhbHcvExportFormConfig;
import net.sf.regadb.util.settings.GhbHcvExportFormConfig.Cell;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.TestItem;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import eu.webtoolkit.jwt.WString;

public class GhbHcvQueryForm extends CustomQuery {
	private DatasetParameter pDataset;

	public GhbHcvQueryForm() {
		super(WString.tr("form.query.custom.ghb-hcv.name"));
	}

	@Override
	protected void init() {
		setName(WString.tr("form.query.custom.ghb-hcv.name").getValue());
		setDescription(WString.tr("form.query.custom.ghb-hcv.description").getValue());
		
		pDataset = new DatasetParameter(this, "Dataset", true);
		
		getParameters().add(pDataset);
	}

	@Override
	public File run() throws Exception {
		File result = File.createTempFile("ghb-hcv_", ".xls", getResultDir());
		PrintStream out = new PrintStream(new FileOutputStream(result));

		XlsDataTable xls = new XlsDataTable(out);
		export(xls);
		xls.flush();

		return result;
	}
	
	private void export(DataTable dt) throws IOException {
		GhbHcvExportFormConfig config = RegaDBSettings.getInstance().getInstituteConfig().getGhbHcvExportFormConfig();
		
		List<String> headers = new ArrayList<String>();
		headers.add("Therapy");
		headers.add("Year of therapy");
		for (String cn : config.getColumnNames()) {
			headers.add(cn);
		}
		
		exportHeader(dt, headers);

		String hql = 
				"select t " +
				"from PatientImpl p, Therapy t " +
				"where p in (" + Transaction.patientsInDatasetSubquery("dataset") + ") " +
				"and t.patient = p " + 
				"order by p.patientId, t.startDate";
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
		Query q = tr.createQuery(hql);
		q.setParameter("dataset", pDataset.getDataset().getDescription());
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		DateFormat therapyDF = new SimpleDateFormat("yyyy");
		
		byte counter = 0;
		while(sr.next()){
			Therapy t = (Therapy)(sr.get()[0]);
			
			List<String> row = new ArrayList<String>();
			row.add(therapyToString(t));
			row.add(therapyDF.format(t.getStartDate()));
			
			for (String cn : config.getColumnNames()) {
				AttributeItem ai = config.getAttributes().get(cn);
				Cell<TestItem> ti = config.getTests().get(cn);
				TestItem tti = config.getTherapyTests().get(cn);
				Cell<EventItem> ei = config.getEvents().get(cn);
				
				if (ai != null) {
					Attribute attribute = tr.getAttribute(ai.name, ai.group);
					PatientAttributeValue pav = tr.getPatientAttributeValue(t.getPatient(), attribute);
					if (pav == null) 
						row.add(null);
					else
						row.add(getValue(pav));
				} else if (ti != null) {
					Test test = tr.getTest(ti.object.description, ti.object.type, ti.object.organism);
					TestResult result = tr.getLatestTestResult(t.getPatient(), test);
					if (result == null) {
						row.add(null);
					} else if (ti.kind == Cell.Kind.Value) {
						row.add(getValue(result));
					} else if (ti.kind == Cell.Kind.Date) {
						SimpleDateFormat df = new SimpleDateFormat(ti.properties.get(GhbHcvExportFormConfig.DATE_FORMAT));
						row.add(df.format(result.getTestDate()));
					}
				} else if (tti != null) {
					Test test = tr.getTest(tti.description, tti.type, tti.organism);
					TestResult found = null;
					for (TestResult result : t.getTestResults()) {
						if (result.getTest().getTestIi() == test.getTestIi()) {
							found = result;
							break;
						}
					}
					if (found == null)
						row.add(null);
					else
						row.add(getValue(found));
				} else if (ei != null) {
					Event event = tr.getEvent(ei.object.name);
					PatientEventValue pev = tr.getLatestEventValue(t.getPatient(), event);
					if (ei.kind == Cell.Kind.Value) {
						if (pev == null) 
							row.add(null);
						else
							row.add(getValue(pev));
					} else {
						throw new RuntimeException("Unsupported export definition for column \"" + cn + "\"");
					}
				} else {
					throw new RuntimeException("No export definition for column \"" + cn + "\"");
				}
			}
			
			exportDataRow(dt, row);
			
			if (counter == 100) {
				counter = 0;
				tr.clearCache();
			} else {
				++counter;
			}
		}
		tr.clearCache();
	}
	
	private String getValue(PatientEventValue pev) {
		if (ValueTypes.isNominal(pev.getEvent().getValueType()))
			return pev.getEventNominalValue().getValue();
		else 
			return pev.getValue();
	}

	private String getValue(TestResult result) {
		if (ValueTypes.isNominal(result.getTest().getTestType().getValueType()))
			return result.getTestNominalValue().getValue();
		else 
			return result.getValue();
	}

	private String getValue(PatientAttributeValue pav) {
		if (ValueTypes.isNominal(pav.getAttribute().getValueType()))
			return pav.getAttributeNominalValue().getValue();
		else 
			return pav.getValue();
	}
	
	private String therapyToString(Therapy t) {
		StringBuilder sb = new StringBuilder();
		
		for (TherapyCommercial tc : t.getTherapyCommercials()) {
			String drug = tc.getId().getDrugCommercial().getName();
			sb.append("+").append(drug);
		}
		
		for (TherapyGeneric tg : t.getTherapyGenerics()) {
			String drug = tg.getId().getDrugGeneric().getGenericName();
			sb.append("+").append(drug);
		}
		
		if (sb.length() > 0)
			return sb.substring(1);
		else
			return "";
	}
	
	private void exportHeader(DataTable t, List<String> headers) throws IOException {
		for (String header : headers) {
			t.addLabel(header);
		}
		t.newRow();
	}

	private void exportDataRow(DataTable t, List<String> rows) throws IOException {
		for (String row : rows) {
			t.addLabel(row);
		}
		t.newRow();
	}
	
	public String getFileName(){
		return getName().toLowerCase().replace(' ', '_') +".xls";
	}
	
	public String getMimeType(){
		return "application/vnd.ms-excel";
	}
}
