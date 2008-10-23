package net.sf.wts.services.util;

public class Job 
{
    public Job(Process process, String sessionTicket)
    {
        process_ = process;
        sessionTicket_ = sessionTicket;
    }
    
    public Process process_;
    public String sessionTicket_;
}
