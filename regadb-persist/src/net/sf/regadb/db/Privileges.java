/*
 * Created on Dec 15, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

public enum Privileges {
    ANONYMOUS_READONLY (1),
    READONLY (2),
    READWRITE (3);
    
    private final int value;
    
    Privileges(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }

    public boolean canWrite() {
        return this.value >= READWRITE.value;
    }
    
    public static Privileges getPrivilege(int permission)
    {
        switch (permission) 
        {
        case 1:
            return Privileges.ANONYMOUS_READONLY;
        case 2:
            return Privileges.READONLY;
        case 3:
            return Privileges.READWRITE;
        }
        
        return null;
    }
}
