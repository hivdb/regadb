package net.sf.regadb.service.wts.client;

import java.io.File;

import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.wts.client.IWtsClient;
import net.sf.wts.client.WtsClient;

public class WtsClientFactory {
	public static IWtsClient getWtsClient(String url) {
		if(url.startsWith("local:")) {
			return new LocalWtsClient(new File(url.substring("local:".length())));
		} else {
			int nrOfRetries = RegaDBSettings.getInstance().getInstituteConfig().getWtsNrOfRetries();
			return new WtsClient(url, nrOfRetries + 1);
		}
	}
}
