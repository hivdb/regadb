package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.ArrayList;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryResult;

public class SuggestedValues {
	private ArrayList<SuggestedValuesOption> options;
	private String query = null;
	private boolean mandatory;
	
	public SuggestedValues() {
		options = new ArrayList<SuggestedValuesOption>();
	}
	
	public SuggestedValues(ArrayList<SuggestedValuesOption> options, String query, boolean mandatory) {
		this.options = options;
		this.query = query;
		this.mandatory = mandatory;
	}
	
	public ArrayList<SuggestedValuesOption> getSuggestedValuesWithoutQuery() {
		return options;
	}
	
	public void addOption(SuggestedValuesOption option) {
		options.add(option);
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public ArrayList<SuggestedValuesOption> getSuggestedValues() {
		ArrayList<SuggestedValuesOption> result = new ArrayList<SuggestedValuesOption>();
		result.addAll(options);

		
		if (query != null) {
			try {
				System.err.println("Trying to execute query: " + query);
				QueryResult rs = DatabaseManager.getInstance().getDatabaseConnector().executeQuery(query);
				System.err.println("found " + rs.size() + " results");
	    		for (int i = 0 ; i < rs.size() ; i++) {
	    			result.add(new SuggestedValuesOption(rs.get(i,0)));
	    		}
	    		rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	return result;
	}
	
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public String getQuery() {
		return query;
	}
}
