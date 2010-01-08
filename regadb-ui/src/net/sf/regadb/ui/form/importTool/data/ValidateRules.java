package net.sf.regadb.ui.form.importTool.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.webtoolkit.jwt.WString;

public class ValidateRules {
	class TypeError {
		TypeError(Rule.Type type, WString error) {
			this.type = type;
			this.error = error;
		}
		
		Rule.Type type;
		WString error;
	}
	
	public WString validateRules(List<Rule> rules) {
		WString error = validatePatientId(rules);
		if (error != null) return error;
		
		error = validateAttributeRules(rules);
		if (error != null) return error;
		
		TypeError testValue = new TypeError(Rule.Type.TestValue, WString.tr("importTool.validate.test.nonUniqueNumber"));
		TypeError testDate = new TypeError(Rule.Type.TestDate, WString.tr("importTool.validate.test.missingDate"));
		error = validateMultipleValues(rules, testValue, testDate);
		if (error != null) return error;
		
		TypeError eventValue = new TypeError(Rule.Type.EventValue, WString.tr("importTool.validate.event.nonUniqueNumber"));
		TypeError eventStartDate = new TypeError(Rule.Type.EventStartDate, WString.tr("importTool.validate.event.missingDate"));
		error = validateMultipleValues(rules, eventValue, eventStartDate);
		if (error != null) return error;
		
		TypeError therapyStartDate = new TypeError(Rule.Type.TherapyStartDate, WString.tr("importTool.validate.therapy.nonUniqueNumber"));
		TypeError regimen = new TypeError(Rule.Type.TherapyRegimen, WString.tr("importTool.validate.therapy.missingRegimen"));
		error = validateMultipleValues(rules, therapyStartDate, regimen);
		if (error != null) return error;
		
		TypeError viSampleId = new TypeError(Rule.Type.ViralIsolateSampleId, WString.tr("importTool.validate.vi.nonUniqueSampleId"));
		TypeError viSampleDate = new TypeError(Rule.Type.ViralIsolateSampleDate, WString.tr("importTool.validate.vi.missingSampleDate"));
		TypeError viSeq1 = new TypeError(Rule.Type.ViralIsolateSampleSequence1, WString.tr("importTool.validate.vi.missingSequence"));
		error = validateMultipleValues(rules, viSampleId, viSampleDate, viSeq1);
		if (error != null) return error;
		
		return null;
	}
	
	private WString validatePatientId(List<Rule> rules) {
		List<Rule> patientIds = new ArrayList<Rule>();
		for (Rule r : rules) {
			if (r.getType() == Rule.Type.PatientId) 
				patientIds.add(r);
		}
		if (patientIds.size() == 0) 
			return WString.tr("importTool.validate.patientId.missing");
		else if (patientIds.size() > 1) 
			return WString.tr("importTool.validate.patientId.multiple");
		else 
			return null;
	}
	
	private WString validateAttributeRules(List<Rule> rules) {
		Set<String> attributes = new HashSet<String>();
		for (Rule r : rules) {
			if (r.getType() == Rule.Type.AttributeValue)
				if (!attributes.add(r.getTypeName())) 
					return WString.tr("importTool.validate.attribute.multiple").arg(r.getTypeName());
		}
		return null;
	}
	
	private WString validateMultipleValues(List<Rule> rules, TypeError central, TypeError ... mandatoryFields) {
		Map<Integer, Rule> centralRules = new HashMap<Integer, Rule>();
		
		for (Rule r : rules) {
			if (r.getType() == central.type) 
				if (centralRules.get(r.getNumber()) != null)
					return central.error.arg(r.getNumber());
				else 
					centralRules.put(r.getNumber(), r);
			
		}
		
		for (TypeError te : mandatoryFields) {
			for (Rule c : centralRules.values()) {
				Rule m_found = null;
				for (Rule m : rules) {
					if (m.getType() == te.type && m.getNumber() == c.getNumber()) 
						m_found = m;
				}
				if (m_found == null) 
					return te.error.arg(c.getNumber());
			}
		}
		
		return null;
	}
}
