package net.sf.regadb.service.wts;

public class ServiceException extends Exception {

    private String service;
    private String url;

    public ServiceException(String service, String url){
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
}
