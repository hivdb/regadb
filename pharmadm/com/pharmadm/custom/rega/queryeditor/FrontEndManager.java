package com.pharmadm.custom.rega.queryeditor;

public class FrontEndManager {
	private FrontEnd frontEnd;
	private static FrontEndManager instance;
	
	private FrontEndManager() {}
	
	public static FrontEndManager getInstance() {
		if (instance == null) {
			instance = new FrontEndManager();
		}
		
		return instance;
	}
	
	public void setFrontEnd(FrontEnd frontEnd) {
		this.frontEnd = frontEnd;
	}
	
	public FrontEnd getFrontEnd() {
		return frontEnd;
	}
}
