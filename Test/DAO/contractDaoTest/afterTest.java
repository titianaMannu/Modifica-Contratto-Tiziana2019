package DAO.contractDaoTest;

import DAO.C3poDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class afterTest {
    /**
     * pulisce l'ambiente per l'esecuzione del test
     * @throws SQLException
     */
    public void clear_table()throws SQLException{
        String clear_table ="delete from ActiveContract where true";
        try(Connection conn = C3poDataSource.getConnection(); Statement st = conn.createStatement()){
            st.executeUpdate(clear_table);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void clear()throws SQLException{
        clear_table();
    }
}
