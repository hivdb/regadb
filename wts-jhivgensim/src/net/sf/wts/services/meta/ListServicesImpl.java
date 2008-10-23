package net.sf.wts.services.meta;

import java.rmi.RemoteException;

import net.sf.wts.services.util.Settings;

public class ListServicesImpl 
{
    public String exec() throws RemoteException
    {
        String toReturn = "";
        boolean first = true;
        for(String s : Settings.getServicesList())
            {
                if(!first)
                {
                    toReturn += '\n';
                }
                else
                {
                    first = false;                    
                }
                toReturn += s;
            }
        return toReturn;
    }
}
