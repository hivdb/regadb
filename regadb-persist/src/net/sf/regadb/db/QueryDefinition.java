package net.sf.regadb.db;

// Generated Jul 18, 2007 4:05:12 PM by Hibernate Tools 3.2.0.beta8

import java.util.HashSet;
import java.util.Set;

/**
 * QueryDefinition generated by hbm2java
 */
public class QueryDefinition implements java.io.Serializable {

    // Fields    

    private Integer queryDefinitionIi;

    private SettingsUser settingsUser;

    private String name;

    private String description;

    private String query;

    private Set<QueryDefinitionParameter> queryDefinitionParameters = new HashSet<QueryDefinitionParameter>(
            0);

    private Set<QueryDefinitionRun> queryDefinitionRuns = new HashSet<QueryDefinitionRun>(
            0);

    // Constructors

    /** default constructor */
    public QueryDefinition() {
    }

    /** full constructor */
    public QueryDefinition(SettingsUser settingsUser, String name,
            String description, String query,
            Set<QueryDefinitionParameter> queryDefinitionParameters,
            Set<QueryDefinitionRun> queryDefinitionRuns) {
        this.settingsUser = settingsUser;
        this.name = name;
        this.description = description;
        this.query = query;
        this.queryDefinitionParameters = queryDefinitionParameters;
        this.queryDefinitionRuns = queryDefinitionRuns;
    }

    // Property accessors
    public Integer getQueryDefinitionIi() {
        return this.queryDefinitionIi;
    }

    public void setQueryDefinitionIi(Integer queryDefinitionIi) {
        this.queryDefinitionIi = queryDefinitionIi;
    }

    public SettingsUser getSettingsUser() {
        return this.settingsUser;
    }

    public void setSettingsUser(SettingsUser settingsUser) {
        this.settingsUser = settingsUser;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Set<QueryDefinitionParameter> getQueryDefinitionParameters() {
        return this.queryDefinitionParameters;
    }

    public void setQueryDefinitionParameters(
            Set<QueryDefinitionParameter> queryDefinitionParameters) {
        this.queryDefinitionParameters = queryDefinitionParameters;
    }

    public Set<QueryDefinitionRun> getQueryDefinitionRuns() {
        return this.queryDefinitionRuns;
    }

    public void setQueryDefinitionRuns(
            Set<QueryDefinitionRun> queryDefinitionRuns) {
        this.queryDefinitionRuns = queryDefinitionRuns;
    }

}
