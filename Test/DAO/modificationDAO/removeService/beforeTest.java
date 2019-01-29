package DAO.modificationDAO.removeService;

import DAO.C3poDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class beforeTest {
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
    public void insert() throws SQLException{
        int res1  = -1, res2 = -1;
        String sql1 = "insert into ActiveContract(idContract, tenantNickname, renterNickname, tenantCF, renterCF, grossPrice, netPrice)\n" +
                "values (1, 'pippo', 'pluto', 'xxx', 'yyy', 2, 1)";
        String sql2 = "insert into OptionalService(activecontract_idcontract, name, price) " +
                "values (1, 'wifi', 0)";
        try(Connection conn = C3poDataSource.getConnection(); Statement st = conn.createStatement()){
            clear_table();
            st.execute("SET FOREIGN_KEY_CHECKS=0"); //disabilito il controllo delle FK in fase di inserimento
            res1 = st.executeUpdate(sql1);
            res2 = st.executeUpdate(sql2);
            st.execute("SET FOREIGN_KEY_CHECKS=1"); //disabilito il controllo delle FK in fase di inserimento
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            assertEquals(1, res1);
            assertEquals(1, res2);
        }
    }


}
