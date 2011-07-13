package net.sf.regadb.service.wts;

public class ServiceException extends Exception {

    private String service;
    private String url;

    public ServiceException(String service, String url){
    	this(service, url, null);
    }
    
    public ServiceException(String service, String url, String msg){
    	super(msg);
        setService(service);
        setUrl(url);
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
        
    public static class ServiceUnavailableException extends ServiceException{

        public ServiceUnavailableException(String service, String url) {
            super(service, url);
        }

    }
    
    public static class InvalidResultException extends ServiceException{
    	
    	public InvalidResultException(String service, String url, String msg){
    		super(service, url, msg);
    	}
    }
}
