/*
 * JDBCManager.java
 *
 * Created on September 8, 2003, 9:18 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

//import com.pharmadm.chem.matter.Molecule;
//import com.pharmadm.custom.rega.domainclasses.ViroDBMolecule;


/**
 * Manages a JBDC connection.
 *
 * @author  kdg
 */
public class JDBCManager {
    
    Connection con;
    private List tableNames = null;
    private DatabaseTableCatalog tableCatalog = new DatabaseTableCatalog();
    
    private static JDBCManager instance;
    private PreparedStatement tableCommentStatement;
    private PreparedStatement columnCommentStatement;
    
//    private Map<BigDecimal, Molecule> molCache = Collections.synchronizedMap(new WeakHashMap<BigDecimal, Molecule>());
    
    /**
     * Creates a new instance of JDBCManager
     *
     * The database parameters used depend on the system properties
     *    queryeditor.driver
     *    queryeditor.user
     *    queryeditor.password
     * These properties can e.g. be set at program start-up time using
     * java -Dqueryeditor.driver=com.my.driver.Class -Dqueryeditor.url=jdbc:...
     */
    private JDBCManager(String url, String user, String pwd) throws SQLException, ClassNotFoundException {
        String driver = System.getProperty("queryeditor.driver");
        if (driver == null) {
            driver = "oracle.jdbc.driver.OracleDriver";
        }
        Class.forName(driver);
        if (url == null) {
            url = System.getProperty("queryeditor.url");
        }
        if (user == null) {
            user = System.getProperty("queryeditor.user");
        }
        if (pwd == null) {
            pwd = System.getProperty("queryeditor.password");
        }
        Properties info = new Properties();
        info.put("user", user);
        info.put("password", pwd);
        // For Oracle to report remarks for tables and columns.
        //info.put("remarksReporting", "true");
        con = DriverManager.getConnection(url, info);
        prepareCommentStatements();
    }
    
    private JDBCManager(Connection conn) throws SQLException {
        this(conn, true);
    }
    
    /**
     * Only for com.pharmadm.custom.rega.visualization.PatientExporter.
     */
    private JDBCManager(Connection conn, boolean prepareStatements) throws SQLException {
        this.con = conn;
        if (prepareStatements) {
            prepareCommentStatements();
        }
    }
    
    /**
     * This method relies completely on environment variables for the login info
     */
    public static void initInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new JDBCManager(null, null, null);
        }
    }
    
    /**
     * Provides login info to make a connection, but with a fallback on environment variables if info is missing.
     */
    public static void initInstance(String url, String user, String pwd) throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new JDBCManager(url, user, pwd);
        }
    }
    
    public static void initInstance(Connection conn) throws SQLException {
        if (instance == null) {
            instance = new JDBCManager(conn);
        }
    }
    
    /**
     * Only for com.pharmadm.custom.rega.visualization.PatientExporter.
     * WARNING: Not compatible with ViroDM.  Do NOT call this method if you want to run
     * ViroDM inside the same JVM.
     */
    public static void initInstance(Connection conn, boolean prepareStatements) throws SQLException {
        if (instance == null) {
            instance = new JDBCManager(conn, prepareStatements);
        }
    }
    
    /**
     * Attempts to initialize an instance using the url, pw and username stored in a properties file.
     * If the initialization fails, getInstance() will be unchanged (null if not yet initialized otherwise).
     * This method is intended for test purposes; it can automate logging in to the database without storing
     * the password in the source code (and thus in the class files that get distributed) or passing it on
     * the command line (which reveals the password to all users on the system).
     */
    public static void attemptInitInstanceWithProperties(String fileName) {
        Properties dbProps = new Properties();
        String home = System.getProperty("user.home");
        File propsFile = new File(home + File.separator + fileName);
        if (propsFile.exists()) {
            InputStream propStream = null;
            try {
                propStream = new BufferedInputStream(new FileInputStream(propsFile));
                dbProps.load(propStream);
                String dbURL = dbProps.getProperty("dbURL");
                String dbPassword = dbProps.getProperty("dbPassword");;
                String dbUser = dbProps.getProperty("dbUser");;
                if (dbURL != null && dbPassword != null && dbUser != null) {
                    try {
                        initInstance(dbURL, dbUser, dbPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    propStream.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
    
    public static JDBCManager getInstance() {
        return instance;
    }
    
    public DatabaseTableCatalog getTableCatalog() {
        return tableCatalog;
    }
    
    public ResultSet executeQuery(String query) throws SQLException {
        // Warning: when reusing statements, don't forget to update the closeQueryResultSet method too.
        // KVB : or even better, don't use closeQueryResultSet for closing ResultSets, because it closes
        //       Statements instead ...
        //       to avoid confusion, closeQueryResultSet is now renamed to closeStatement ;
        //       for closing ResultSets, you can use resultSet.close()
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(50);
        ResultSet srs = stmt.executeQuery(query);
        return srs;
    }
    
    public int executeUpdate(String updateSQL) throws SQLException {
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return stmt.executeUpdate(updateSQL);
    }
    
    public void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException sqle) {
                System.err.println("Problem while trying to close query statement.");
            } catch (NullPointerException npe) {
                System.err.println("Nullpointer problem while trying to close query statement - probably harmless.");
            }
        }
    }
    
    public void closeStatement(ResultSet rs) {
        if (rs != null) {
            try {
                Statement stmt = rs.getStatement();
                stmt.close();  // closes resultset too
            } catch (SQLException sqle) {
                System.err.println("Problem while trying to close query statement.");
            } catch (NullPointerException npe) {
                System.err.println("Nullpointer problem while trying to close query statement - probably harmless.");
            }
        }
    }
    
    public List getTableNames() {
        if (tableNames == null) {
            try {
                DatabaseMetaData dmd = con.getMetaData();
                ResultSet rs = dmd.getTables(null, null, null, null);
                tableNames = new ArrayList();
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
                rs.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return tableNames;
    }
    
    public List getColumnNames(String tableName) {
        tableName = tableName.toUpperCase();
        List result = new ArrayList();
        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getColumns(null, null, tableName, null);
            while (rs.next()) {
                result.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return result;
    }
    
    public String getColumnType(String tableName, String columnName) {
        tableName = tableName.toUpperCase();
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
    
    public List getPrimaryKeys(String tableName) {
        tableName = tableName.toUpperCase();
        List result = new ArrayList();
        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                result.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return result;
    }
    
    public String getCommentForTable(String tableName) {
        String comment = null;
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
    
    public String getCommentForColumn(String tableName, String fieldName) {
        String comment = null;
        if (columnCommentStatement != null) {
            try {
                columnCommentStatement.setString(1, tableName);
                columnCommentStatement.setString(2, fieldName);
                ResultSet rs = columnCommentStatement.executeQuery();
                try {
                    if (rs.next()) {
                        comment = rs.getString(1);
                    }
                } finally {
                    rs.close();
                }
            } catch (SQLException sqle) {
                System.err.println("Could not retrieve comment for column " + fieldName + " in table " + tableName + ": " + sqle.getMessage());
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
    
    public PreparedStatement prepareStatement(String sql) {
        try {
            return con.prepareStatement(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    
    public CallableStatement prepareCall(String sql) {
        try {
            return con.prepareCall(sql);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }
    
    public Statement createScrollableReadOnlyStatement() throws SQLException {
        return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }
    
    /**
     * Whether the user has full read/write access to a table.
     */
    public boolean hasCRUDPermissions(String tableName) {
        boolean canSelect = false;
        boolean canInsert = false;
        boolean canUpdate = false;
        boolean canDelete = false;
        if (tableName != null && !"".equals(tableName)) {
            try {
                DatabaseMetaData dmd = con.getMetaData();
                ResultSet rs = dmd.getTablePrivileges(null, null, tableName);
                while ((!canSelect || !canInsert || !canUpdate || !canDelete) && rs.next()) {
                    String priv = rs.getString("PRIVILEGE");
                    canSelect = canSelect || "SELECT".equals(priv);
                    canInsert = canInsert || "INSERT".equals(priv);
                    canUpdate = canUpdate || "UPDATE".equals(priv);
                    canDelete = canDelete || "DELETE".equals(priv);
                }
                rs.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return canSelect && canInsert && canUpdate && canDelete;
    }
    
    /**
     * Whether the user has insert access to a table.
     */
    public boolean hasInsertPermission(String tableName) {
        boolean canInsert = false;
        if (tableName != null && !"".equals(tableName)) {
            try {
                DatabaseMetaData dmd = con.getMetaData();
                ResultSet rs = dmd.getTablePrivileges(null, null, tableName);
                while ((!canInsert) && rs.next()) {
                    String priv = rs.getString("PRIVILEGE");
                    canInsert = canInsert || "INSERT".equals(priv);
                }
                rs.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return canInsert;
    }
    
    public String getUserName() throws SQLException {
        return con.getMetaData().getUserName();
    }
    
    /**
     * Loads a molecule from the database or from the in-memory cache.
     */
//    public Molecule getMolecule(BigDecimal ii) {
//        Molecule mol = null;
//        try {
//            mol = getMoleculeAllowException(ii);
//        } catch (SQLException sqle) {
//            sqle.printStackTrace();
//        }
//        return mol;
//    }
    
    /**
     * Loads a molecule from the database or from the in-memory cache.
     */
//    public Molecule getMoleculeAllowException(BigDecimal ii) throws SQLException {
//        Molecule mol = null;
//        if (ii != null) {
//            mol = molCache.get(ii);
//            if (mol == null) {
//                mol = new ViroDBMolecule(ii);
//                molCache.put(ii, mol);
//            }
//        }
//        return mol;
//    }
//    
//    public void clearMoleculeCache() {
//        molCache.clear();
//    }
}
