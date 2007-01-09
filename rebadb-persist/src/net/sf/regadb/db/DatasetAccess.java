package net.sf.regadb.db;

// Generated 9/01/2007 13:30:52 by Hibernate Tools 3.2.0.beta8

/**
 * DatasetAccess generated by hbm2java
 */
public class DatasetAccess implements java.io.Serializable {

    // Fields    

    private DatasetAccessId id;

    private Integer version;

    private Dataset dataset;

    private SettingsUser settingsUser;

    private int permissions;

    // Constructors

    /** default constructor */
    public DatasetAccess() {
    }

    /** full constructor */
    public DatasetAccess(DatasetAccessId id, Dataset dataset,
            SettingsUser settingsUser, int permissions) {
        this.id = id;
        this.dataset = dataset;
        this.settingsUser = settingsUser;
        this.permissions = permissions;
    }

    // Property accessors
    public DatasetAccessId getId() {
        return this.id;
    }

    public void setId(DatasetAccessId id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Dataset getDataset() {
        return this.dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public SettingsUser getSettingsUser() {
        return this.settingsUser;
    }

    public void setSettingsUser(SettingsUser settingsUser) {
        this.settingsUser = settingsUser;
    }

    public int getPermissions() {
        return this.permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

}
