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
 * Object that represents an authenticated login for access
 * to the database.
 */
public class Login {
    private String uid;

    static Login authenticate(String uid, String passwd) {
        Login login = new Login(uid);
        
        if (login.authenticate(passwd)) {
            return login;
        } else
            return null;
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

    public Transaction createTransaction() {
        return new Transaction(this, getSession());
    }
    
    private Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    public String getUid() {
        return uid;
    }
}
