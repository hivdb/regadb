/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.ILoginStrategy;
import net.sf.regadb.db.login.LoginFactory;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.util.settings.AttributeConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 * Login represents an authenticated login for access to the
 * database.
 * 
 * A Login does not maintain an open connection to the database,
 * or any other resources, and is safe to to keep around for
 * a long time in your application.
 */
public class Login {
    private String uid;
    private Session session_;

    /**
     * Create a new authenticated login to the database.
     * 
     * Uses the database settings from hibernate.
     * 
     * @param uid
     * @param passwd
     * @return
     * @throws WrongPasswordException 
     * @throws WrongUidException 
     */
    public static Login authenticate(String uid, String passwd) throws WrongUidException, WrongPasswordException, DisabledUserException {
        Login login = new Login(uid, true);
        
        ILoginStrategy loginMethod = LoginFactory.getLoginInstance();
        
        SettingsUser su = loginMethod.authenticate(uid, passwd, login);
        
        if(su.getRole()==null || su.getRole().length() == 0)
        {
            throw new DisabledUserException();
        }
        
        if (su!=null)
        {
            return login;
        } 
        else
        {
        	return null;
        }
    }
    
    public static Login getLogin(String uid){
    	return new Login(uid, true);
    }
    
    /**
     * Start a new transaction.
     * 
     * @return
     */
    public Transaction createTransaction() {
        return new Transaction(this, getSession());
    }
    
    public String getUid() {
        return uid;
    }

    private Session getSession() {
        return session_;
    }
    
    private Login(String uid, boolean blockAttributes) {
        this.uid = uid;
        session_ = HibernateUtil.getSessionFactory().openSession();
        
        Transaction t  = new Transaction(this, getSession());
        SettingsUser su = t.getSettingsUser(uid);
        
        List<AttributeConfig> attributes = RegaDBSettings.getInstance().getAccessPolicyConfig().getRole(su.getRole()).getBlockedAttributes();
        if(blockAttributes && attributes!=null) {
        	Set<Integer> attribute_iis = new HashSet<Integer>(attributes.size());
        	Attribute attribute;
        	for(AttributeConfig a : attributes) {
        		attribute = t.getAttribute(a.getName(), a.getGroup());
        		if(attribute!=null) {
        			attribute_iis.add(attribute.getAttributeIi());
        		} else {
        			System.err.println("Blocked Attribute cannot be found: " + a.getGroup() + " - " +a.getName());
        		}
        	}
        	
        	if(attribute_iis.size()>0)
        		session_.enableFilter("attributeFilter").setParameterList("attribute_ii_list", attribute_iis);
        }
        
        prepareQueries();
    }
    
    public Login copyLogin()
    {
        return copyLogin(true);
    }
    
    public Login copyLogin(boolean blockAttributes) {
    	return new Login(this.uid, blockAttributes);
    }
    
    private void prepareQueries()
    {
    }
    
    public static void createNewAccount(SettingsUser user)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    public void closeSession() {
        if("org.hibernate.dialect.HSQLDialect".equals(RegaDBSettings.getInstance().getHibernateConfig().getDialect())) {
            SQLQuery shutdown = session_.createSQLQuery("SHUTDOWN");
            shutdown.executeUpdate();
        }
        session_.close();
    }
}
