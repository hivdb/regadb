package com.pharmadm.custom.rega.queryeditor.catalog;

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.constant.BooleanConstant;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.DateConstant;
import com.pharmadm.custom.rega.queryeditor.constant.DoubleConstant;
import com.pharmadm.custom.rega.queryeditor.constant.OperatorConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.SuggestedValuesOption;

public class HibernateCatalogUtils {

	public static OperatorConstant getCalculationOperator(DbObject object) {
		ValueType type = object.getValueType();
		if (type == ValueType.String) {
			return getStringCalculationOperator();
		}
		else if (type == ValueType.Number) {
			return getNumberCalculationOperator();
		}
		else if (type == ValueType.Date) {
			return getDateCalculationOperator();
		}
		else {
			return new OperatorConstant();
		}
		
	}
	
	/**
	 * Get a constant with the possible calculation operations for dates: (+, -)
	 * @return a constant with the possible calculation operations for dates
	 */
	private static OperatorConstant getDateCalculationOperator() {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("+", "+"));
		constant.addSuggestedValue(new SuggestedValuesOption("-", "-"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible calculation operations for strings: (||)
	 * @return a constant with the possible calculation operations for strings
	 */
	private static OperatorConstant getStringCalculationOperator() {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("||", "+"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible calculation operations for numbers: (+, -, *, /)
	 * @return a constant with the possible calculation operations for numbers
	 */
	private static OperatorConstant getNumberCalculationOperator() {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("+", "+"));
		constant.addSuggestedValue(new SuggestedValuesOption("-", "-"));
		constant.addSuggestedValue(new SuggestedValuesOption("*", "*"));
		constant.addSuggestedValue(new SuggestedValuesOption("/", "/"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible comparison operations for checking an interval: (between, not between)
	 * @return a constant with the possible comparison operations for checking an interval
	 */
	public static OperatorConstant getIntervalComparisonOperator() {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("BETWEEN", "is between"));
		constant.addSuggestedValue(new SuggestedValuesOption("NOT BETWEEN", "is not between"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible comparison operations for numeric values: (=, <>, <, >)
	 * @return a constant with the possible comparison operations for numeric values
	 */
	private static OperatorConstant getNumberComparisonOperator(boolean exact) {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
		constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
		if (!exact) {
	       	constant.addSuggestedValue(new SuggestedValuesOption("<", "is less than"));
	    	constant.addSuggestedValue(new SuggestedValuesOption(">", "is more than"));
		}
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible comparison operations for string values: (like, not like)
	 * @return a constant with the possible comparison operations for string values
	 */
	private static OperatorConstant getStringComparisonOperator(boolean exact) {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("LIKE", "is"));
		constant.addSuggestedValue(new SuggestedValuesOption("NOT LIKE", "is not"));
		if (!exact) {
	    	constant.addSuggestedValue(new SuggestedValuesOption("<", "comes before"));
	    	constant.addSuggestedValue(new SuggestedValuesOption(">", "comes after"));
		}
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible comparison operations for boolean values: (=, <>)
	 * @return a constant with the possible comparison operations for boolean values
	 */
	private static OperatorConstant getBooleanComparisonOperator() {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
		constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible comparison operations for date values: (=, <>, <, >)
	 * @param property the property to fetch the date comparator of
	 * @param exact true when comparison may only be exact
	 * @return a constant with the possible comparison operations for date values
	 */
	private static OperatorConstant getDateComparisonOperator(boolean exact) {
		OperatorConstant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("="," is on"));
		constant.addSuggestedValue(new SuggestedValuesOption("<>", " is not on"));
		if (!exact) {
	       	constant.addSuggestedValue(new SuggestedValuesOption("<", "is before"));
	       	constant.addSuggestedValue(new SuggestedValuesOption("<=", "is before or on"));
	    	
	       	constant.addSuggestedValue(new SuggestedValuesOption(">", "is after"));
	       	constant.addSuggestedValue(new SuggestedValuesOption(">=", "is after or on"));
		}
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	 * Get a constant with the possible comparison operations for persistent objects: (=, <>)
	 * @return a constant with the possible comparison operations for persistent objects
	 */
	private static OperatorConstant getObjectComparisonOperator() {
		OperatorConstant constant = new OperatorConstant();
		constant.addSuggestedValue(new SuggestedValuesOption("=", "is"));
		constant.addSuggestedValue(new SuggestedValuesOption("<>", "is not"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	/**
	* gets a constant with the possible comparison operators for NULL tests (IS NULL, IS NOT NULL)
	* @return a constant with the possible comparison operators for NULL tests
	*/
    public static OperatorConstant getNullComparisonOperator() {
    	OperatorConstant constant = new OperatorConstant();
    	constant.addSuggestedValue(new SuggestedValuesOption("IS NOT NULL", "is defined"));
    	constant.addSuggestedValue(new SuggestedValuesOption("IS NULL", "is not defined"));
    	constant.setSuggestedValuesMandatory(true);
    	return constant;
    }

	public static Constant getConstant(DbObject object, String suggestedValuesQuery) {
		ValueType type = object.getValueType();
		Constant constant = null;
		if (type == ValueType.String) {
			constant = new StringConstant();
		}
		else if (type == ValueType.Boolean) {
			constant = new BooleanConstant();
		}
		else if (type == ValueType.Number) {
			constant = new DoubleConstant();
		}
		else if (type == ValueType.Date) {
			constant = new DateConstant();
		}
		if (constant != null && type != ValueType.Boolean) {
			if (object.hasDropdown() && suggestedValuesQuery == null) {
				suggestedValuesQuery = "SELECT DISTINCT obj." + object.getPropertyName() + " FROM " + object.getTableName() + " obj";
				constant.setSuggestedValuesQuery(suggestedValuesQuery);
	    		constant.setSuggestedValuesMandatory(true);
			}
			else if (suggestedValuesQuery != null){
				constant.setSuggestedValuesQuery(suggestedValuesQuery);
	    		constant.setSuggestedValuesMandatory(true);
			}
		}
		return constant;
	}

	public static boolean isCaseSensitive(DbObject object) {
		ValueType type = object.getValueType();
		if (type == ValueType.String) {
			return false;
		}
		else {
			return true;
		}
	}

	public static Constant getComparisonOperator(DbObject object, boolean exact) {
		ValueType type = object.getValueType();
		if (type == ValueType.String) {
			return getStringComparisonOperator(exact);
		}
		else if (type == ValueType.Boolean) {
			return getBooleanComparisonOperator();
		}
		else if (type == ValueType.Number) {
			return getNumberComparisonOperator(exact);
		}
		else if (type == ValueType.Date) {
			return getDateComparisonOperator(exact);
		}
		else {
			return getObjectComparisonOperator();
		}
	}

	private static Constant getDateAggregateFunction() {
		Constant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("min", "earliest"));
	   	constant.addSuggestedValue(new SuggestedValuesOption("max", "latest"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	private static Constant getNumberAggregateFunction() {
		Constant constant = new OperatorConstant();
	   	constant.addSuggestedValue(new SuggestedValuesOption("min", "lowest"));
	   	constant.addSuggestedValue(new SuggestedValuesOption("max", "highest"));
		constant.setSuggestedValuesMandatory(true);
		return constant;
	}

	public static Constant getAggregateFunction(DbObject object) {
		ValueType type = object.getValueType();
		if (type == ValueType.String) {
			return new OperatorConstant();
		}
		else if (type == ValueType.Boolean) {
			return new OperatorConstant();
		}
		else if (type == ValueType.Number) {
			return getNumberAggregateFunction();
		}
		else if (type == ValueType.Date) {
			return getDateAggregateFunction();
		}
		else {
			return new OperatorConstant();
		}
	}
}
