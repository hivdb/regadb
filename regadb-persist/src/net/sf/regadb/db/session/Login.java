/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.session;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.ILoginStrategy;
import net.sf.regadb.db.login.LoginFactory;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;

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
    public static Login authenticate(String uid, String passwd) throws WrongUidException, WrongPasswordException {
        Login login = new Login(uid);
        
        ILoginStrategy loginMethod = LoginFactory.getLoginInstance();
        
        if (loginMethod.authenticate(uid, passwd, login)!=null) 
        {
            return login;
        } 
        else
        {
        	return null;
        }
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
    
    private Login(String uid) {
        this.uid = uid;
        session_ = HibernateUtil.getSessionFactory().openSession();
    }
    
    public Login copyLogin()
    {
        return new Login(this.uid);
    }
    
    public static void createNewAccount(SettingsUser user)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }
}
