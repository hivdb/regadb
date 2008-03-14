package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface DatabaseConnector {
    public List<String> getTableNames();
    public List<String> getPrimaryKeys(String tableName);
    public List<String> getColumnNames(String tableName);
    public String getColumnType(String tableName, String columnName);   
    public QueryResult executeQuery(String query) throws SQLException;
    public QueryStatement createScrollableReadOnlyStatement() throws SQLException;
    
    public String getCommentForTable(String tableName);
    public String getCommentForColumn(String tableName, String columnName);
}
