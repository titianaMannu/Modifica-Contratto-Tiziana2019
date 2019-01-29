package DAO.modificationDAO;

import DAO.C3poDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

public class afterTest {
    /**
     * pulisce l'ambiente per l'esecuzione del test
     * @throws SQLException
     */
    public boolean clear_table(){
        String clear_contracts ="delete from ActiveContract where true";
        String clear_services = "delete from OptionalService where true";
        try(Connection conn = C3poDataSource.getConnection(); Statement st = conn.createStatement()){
            st.executeUpdate(clear_contracts);
            st.executeUpdate(clear_services);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Test
    public void clear()throws SQLException{
        assertTrue(clear_table());
    }
}
