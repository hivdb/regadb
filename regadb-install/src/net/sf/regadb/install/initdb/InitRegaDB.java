package net.sf.regadb.install.initdb;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.util.encrypt.Encrypt;

import org.hibernate.Session;

public class InitRegaDB 
{
    public static void main(String [] args)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        SettingsUser admin = new SettingsUser("admin", 0, 0);
        admin.setFirstName("install-admin");
        admin.setLastName("install-admin");
        admin.setAdmin(true);
        admin.setEnabled(true);
        admin.setPassword(Encrypt.encryptMD5("admin"));
        session.save(admin);
        session.getTransaction().commit();
        session.close();
    }
}
