package net.sf.regadb.util.settings;

import net.sf.regadb.util.settings.ProxyConfig.ProxyServer;


public class ConfigTest {

	public static void main(String args[]){
		RegaDBSettings set = RegaDBSettings.getInstance();
		set = new RegaDBSettings();
		
		set.getInstituteConfig().getSelectPatientFormConfig().setAttributeFilter(new AttributeConfig("Gender", "Personal"));
		
		set.getProxyConfig().getProxyList().add(new ProxyConfig.ProxyServer("www-proxy","3128","user1","pass1",ProxyServer.Type.HTTP));
		
		WivConfig wiv = new WivConfig();
		wiv.setArcPatientFilter(new AttributeConfig("FOLLOW-UP","WIV","1: ARC of the same institution as ARL"));
		wiv.setCentreName("KUL");
		wiv.setViralLoadTest(new TestTypeConfig("Viral Load (copies/ml)","HIV-1"));
		set.getInstituteConfig().setWivConfig(wiv);
		
		Role r = new Role();
		r.setAdmin(false);
		r.setName("user");
		r.setSinglePatientView(true);
		r.getBlockedAttributes().add(new AttributeConfig("First name","Personal"));
		set.getAccessPolicyConfig().addRole(r);
		
		set.write(new java.io.File("/dev/null"));
	}
}
