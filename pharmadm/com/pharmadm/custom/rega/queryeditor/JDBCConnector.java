package com.pharmadm.custom.rega.queryeditor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JDBCConnector implements  DatabaseConnector{
	private Connection con;
	private PreparedStatement columnCommentStatement;
	private PreparedStatement tableCommentStatement;
	
    /**
     * Creates a new instance of JDBCConector
     *
     * The database parameters used depend on the system properties
     *    queryeditor.driver
     *    queryeditor.user
     *    queryeditor.password
     * These properties can e.g. be set at program start-up time using
     * java -Dqueryeditor.driver=com.my.driver.Class -Dqueryeditor.url=jdbc:...
     */
    public JDBCConnector(String driver, String url, String user, String pwd) throws SQLException, ClassNotFoundException {
        if (driver == null) {
        	driver = "org.postgresql.Driver";
        }
        Class.forName(driver);
        Properties info = new Properties();
        info.put("user", user);
        info.put("password", pwd);
        Connection conn = DriverManager.getConnection(url, info);
        init(conn, true);
    }

    private void init(Connection conn, boolean prepareStatements) {
        this.con = conn;
        if (prepareStatements) {
            prepareCommentStatements();
        }
    }
    
    public JDBCConnector(Connection conn) throws SQLException {
        this(conn, true);
    }
    
    /**
     * Only for com.pharmadm.custom.rega.visualization.PatientExporter.
     */
    private JDBCConnector(Connection conn, boolean prepareStatements) throws SQLException {
    	init(conn, prepareStatements);
    }
    
	@Override
	public QueryResult executeQuery(String query) throws SQLException {
    	// Warning: when reusing statements, don't forget to update the closeQueryResultSet method too.
        // KVB : or even better, don't use closeQueryResultSet for closing ResultSets, because it closes
        //       Statements instead ...
        //       to avoid confusion, closeQueryResultSet is now renamed to closeStatement ;
        //       for closing ResultSets, you can use resultSet.close()
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(50);
        ResultSet srs = stmt.executeQuery(query);
        return new JDBCResult(srs);
	}

	@Override
	public List<String> getColumnNames(String tableName) {
        List<String> result = new ArrayList<String>();
        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getColumns(null, null, tableName, null);
            while (rs.next()) {
                result.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return result;
	}

	@Override
	public String getColumnType(String tableName, String columnName) {
        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getColumns(null, null, tableName, null);
            while (rs.next()) {
                if (rs.getString("COLUMN_NAME").equalsIgnoreCase(columnName)) {
                    return rs.getString("DATA_TYPE");
                }
            }
            rs.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
	}

	@Override
	public List<String> getPrimaryKeys(String tableName) {
        List<String> result = new ArrayList<String>();
        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                result.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return result;
	}

	@Override
	public List<String> getTableNames() {
        List<String> names = new ArrayList<String>();
    	try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getTables(null, null, null, null);
            while (rs.next()) {
                names.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
        } 
    	catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    	return names;
	}
	
    private PreparedStatement prepareStatement(String sql) {
        try {
            return con.prepareStatement(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

	@Override
	public String getCommentForColumn(String tableName, String columnName) {
        String comment = null;
        PreparedStatement columnCommentStatement = getColumnCommentStatement();
        if (columnCommentStatement != null) {
            try {
                columnCommentStatement.setString(1, tableName);
                columnCommentStatement.setString(2, columnName);
                ResultSet rs = columnCommentStatement.executeQuery();
                try {
                    if (rs.next()) {
                        comment = rs.getString(1);
                    }
                } finally {
                    rs.close();
                }
            } catch (SQLException sqle) {
                System.err.println("Could not retrieve comment for column " + columnName + " in table " + tableName + ": " + sqle.getMessage());
            }
        }
        return comment;
	}

	@Override
	public String getCommentForTable(String tableName) {
        String comment = null;
        PreparedStatement tableCommentStatement = getTableCommentStatement();
        if (tableCommentStatement != null) {
            try {
                tableCommentStatement.setString(1, tableName);
                ResultSet rs = tableCommentStatement.executeQuery();
                try {
                    if (rs.next()) {
                        comment = rs.getString(1);
                    }
                } finally {
                    rs.close();
                }
            } catch (SQLException sqle) {
                System.err.println("Could not retrieve comment for table " + tableName + ": " + sqle.getMessage());
            }
        }
        return comment;
	}
	
    private void prepareCommentStatements() {
        // WARNING: THIS STATEMENT CONTAINS A REGA/ViroDM-SPECIFIC IDENTIFIER.
        // USING DatabaseMetaData INSTEAD WOULD BE CROSS-PLATFORM, BUT USES TOO MANY RESOURCES ON ORACLE 9.0.1.
        tableCommentStatement  = prepareStatement("SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE lower(TABLE_NAME)=lower(?) AND TABLE_TYPE='TABLE' AND OWNER='VIRO'");
        columnCommentStatement = prepareStatement("SELECT COMMENTS FROM ALL_COL_COMMENTS WHERE lower(TABLE_NAME)=lower(?) AND lower(COLUMN_NAME)=lower(?) AND OWNER='VIRO'");
    }

	private PreparedStatement getColumnCommentStatement() {
		return columnCommentStatement;
	}

	private PreparedStatement getTableCommentStatement() {
		return tableCommentStatement;
	}

	@Override
    public QueryStatement createScrollableReadOnlyStatement() throws SQLException {
        return new JDBCStatement(con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));
    }
}
