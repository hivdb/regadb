package net.sf.regadb.service.wts;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.service.wts.ServiceException.ServiceUnavailableException;
import net.sf.regadb.service.wts.client.WtsClientFactory;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.wts.client.IWtsClient;

public abstract class AbstractService {
    
    private Date startTime;
    private Date endTime;
    private int waitDelay;

    private String url = RegaDBWtsServer.getUrl();
    private String account = "public";
    private String password = "public";
    private String service;

    private Map<String,String> inputs = new HashMap<String,String>();
    private Map<String,String> outputs = new HashMap<String,String>();
    
    protected abstract void init();
    protected abstract void processResults() throws ServiceException;

    public void launch() throws ServiceException
    {
        setStartTime(new Date());
        init();
        
        IWtsClient client_ = WtsClientFactory.getWtsClient(
        		RegaDBSettings.getInstance().getInstituteConfig().getWtsUrl(getUrl()));
        
        String challenge;
        String ticket = null;

        try 
        {
            challenge = client_.getChallenge(getAccount());
            ticket = client_.login(getAccount(), challenge, getPassword(), getService());
        
            for(Map.Entry<String,String> entry : getInputs().entrySet()){
                client_.upload(ticket, getService(), entry.getKey(), entry.getValue().getBytes());
            }
            
            client_.start(ticket, getService());
            
            boolean finished = false;
            while(!finished)
            {
                try 
                {
                    Thread.sleep(getWaitDelay());
                } 
                catch (InterruptedException ie) 
                {
                    ie.printStackTrace();
                    throw new RuntimeException(ie);
                }
                if(client_.monitorStatus(ticket, getService()).startsWith("ENDED"))
                {
                    finished = true;
                }
            }

            for(Map.Entry<String,String> entry : getOutputs().entrySet()){
                byte [] resultArray = client_.download(ticket, getService(), entry.getKey());
                entry.setValue(new String(resultArray));
            }
            
            client_.closeSession(ticket, getService());
        } 
        catch (RemoteException e1) 
        {
            e1.printStackTrace();
            throw new ServiceUnavailableException(getService(),getUrl());
        } 
        catch (MalformedURLException e) 
        {
            e.printStackTrace();
            throw new ServiceUnavailableException(getService(),getUrl());
        }
        
        processResults();
        setEndTime(new Date());
    }


    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setWaitDelay(int waitDelay) {
        this.waitDelay = waitDelay;
    }

    public int getWaitDelay() {
        return waitDelay;
    }

    public void setInputs(Map<String,String> inputs) {
        this.inputs = inputs;
    }

    public Map<String,String> getInputs() {
        return inputs;
    }

    public void setOutputs(Map<String,String> outputs) {
        this.outputs = outputs;
    }

    public Map<String,String> getOutputs() {
        return outputs;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
