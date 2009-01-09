package net.sf.regadb.db.meta;

import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ValueType;

public class Ids {

	public static String getUniqueId(EventNominalValue eventNominalValue) {
		return eventNominalValue.getNominalValueIi()+"";
	}

	public static String getUniqueId(Event event) {
		return event.getEventIi()+"";
	}

	public static String getUniqueId(ValueType valueType) {
		return valueType.getValueTypeIi()+"";
	}

	public static String getUniqueId(Test test) {
		return test.getTestIi()+"";
	}

	public static String getUniqueId(TestNominalValue testNominalValue) {
		return testNominalValue.getNominalValueIi()+"";
	}

	public static String getUniqueId(Analysis analysis) {
		return analysis.getAnalysisIi()+"";
	}

	public static String getUniqueId(TestType testType) {
		return testType.getTestTypeIi()+"";
	}

	public static String getUniqueId(Attribute attribute) {
		return attribute.getAttributeIi()+"";
	}

	public static String getUniqueId(AttributeNominalValue attributeNominalValue) {
		return attributeNominalValue.getNominalValueIi()+"";
	}

	public static String getUniqueId(TestObject testObject) {
		return testObject.getTestObjectIi()+"";
	}

	public static String getUniqueId(AnalysisData analysisData) {
		return analysisData.getAnalysisDataIi()+"";
	}

	public static String getUniqueId(AttributeGroup attributeGroup) {
		return attributeGroup.getAttributeGroupIi()+"";
	}

}
