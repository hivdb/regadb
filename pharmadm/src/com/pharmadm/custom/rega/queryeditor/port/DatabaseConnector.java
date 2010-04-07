package com.pharmadm.custom.rega.queryeditor.port;

import java.sql.SQLException;
import java.util.List;


public interface DatabaseConnector {
    public List<String> getTableNames();
    public List<String> getPrimaryKeys(String tableName);
    public List<String> getPrimitiveColumnNames(String tableName);
    public List<String> getNonPrimitiveColumnNames(String tableName);
    
    
    /**
     * returns the sql datatype for the given column
     * @param tableName
     * @param columnName
     * @return
     */
    public int getColumnType(String tableName, String columnName);   
    public QueryResult executeQuery(String query) throws SQLException;
    
    public String getCommentForTable(String tableName);
    public String getCommentForColumn(String tableName, String columnName);
    
    public void close();
}
