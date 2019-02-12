package DAO.contractDaoTest;

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
        String sql1 = "insert into ActiveContract(idContract, tenantNickname, renterNickname, tenantCF, renterCF, grossPrice, netPrice, terminationDate)\n" +
                "values (1, 'pippo', 'pluto', 'xxx', 'yyy', 2, 1, '2021-01-03')";
        String sql2 = "insert into OptionalService(activecontract_idcontract, name, price)" +
                " value(1, 'wifi', 0)";
        try(Connection conn = C3poDataSource.getConnection(); Statement st = conn.createStatement()){
            clear_table();
            res1 = st.executeUpdate(sql1);
            res2 = st.executeUpdate(sql2);
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
