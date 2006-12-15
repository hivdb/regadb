/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.session;

import org.hibernate.Session;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;

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

    /**
     * Create a new authenticated login to the database.
     * 
     * Uses the database settings from hibernate.
     * 
     * @param uid
     * @param passwd
     * @return
     */
    public static Login authenticate(String uid, String passwd) {
        Login login = new Login(uid);
        
        if (login.authenticate(passwd)) {
            return login;
        } else
            return null;
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
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
    
    private Login(String uid) {
        this.uid = uid;
    }
    
    private boolean authenticate(String passwd) {
        Transaction t = createTransaction();
        
        SettingsUser settings
            = t.getSettingsUser(uid, passwd);
        
        t.commit();
        
        return settings != null;
    }
}
