package net.sf.wts.services.util;

import java.security.Key;

public class Session {
	
	public String sessionTicket_;
	private Key sessionKey_;
	
	public Session(String sessionTicket_){
		this.sessionTicket_ = sessionTicket_;
		sessionKey_ = Encrypt.getNewSessionKey();	
	}
	
	public Key getSessionKey(){
		return sessionKey_;
	}
	
	public boolean equals(Object o){
		return sessionTicket_.equals(((Session) o).sessionTicket_);		
	}
	

}
