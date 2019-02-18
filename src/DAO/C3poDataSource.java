package DAO;

import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;
import java.sql.*;

/**
 * connection pooling generator in order to optimize the connection time's costs
 */
public class C3poDataSource {
    private static ComboPooledDataSource  cpds = new ComboPooledDataSource();
    static {
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver");
            cpds.setJdbcUrl("jdbc:mysql://localhost:3306/FERSA");
            cpds.setUser("modificatore");
            cpds.setPassword("M.odificatore");
        } catch (PropertyVetoException e) {
            System.exit(-1);
        }
    }

    public static  Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    private C3poDataSource(){}

    //semplice implementazione per ottenere una connessione
    public static Connection getSimpleConnection() throws ClassNotFoundException, SQLException{
        Connection conn = null;
        //loading dinamico del driver mysql
        Class.forName("com.mysql.jdbc.Driver");
        //url
        String dburl = "jdbc:mysql://localhost/FERSA?user=modificatore&password=M.odificatore";
        //apertura connessione
        conn = DriverManager.getConnection(dburl);

        return conn;
    }

}
