package DAO;

import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
            // handle the exception
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    private C3poDataSource(){}

    public static void main(String args[]){
        /**
         * semplice prova per vedere se funziona!
         * just ignore the following lines
         */
        try (Connection con = C3poDataSource.getConnection()) {
            Statement st = con.createStatement();
            String sql = " select * from ActiveContract";
            ResultSet res = st.executeQuery(sql);
            if (res.next())
                System.out.println(res.getString("tenantNinckname"));
        }catch (SQLException e){
            e.printStackTrace();
            System.exit(-2);
        }
    }
}
