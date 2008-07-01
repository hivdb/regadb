package net.sf.regadb.service.wts;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.wts.client.WtsClient;

public class DescribeMutations {
    public static byte[] describeMutations(byte[] asi) {
        WtsClient client_ = new WtsClient(RegaDBWtsServer.url_);

        String challenge;
        String ticket = null;
        String serviceName = "regadb-hiv-describe-mutations";
        try {

            challenge = client_.getChallenge("public");
            ticket = client_.login("public", challenge, "public", serviceName);

            client_.upload(ticket, serviceName, "asi_rules", asi);

            client_.start(ticket, serviceName);

            boolean finished = false;
            while (!finished) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                if (client_.monitorStatus(ticket, serviceName).startsWith(
                        "ENDED")) {
                    finished = true;
                }
            }

            byte[] resultArray = client_.download(ticket, serviceName,
                    "description");

            client_.closeSession(ticket, serviceName);

            return resultArray;
        } catch (RemoteException e1) {
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
