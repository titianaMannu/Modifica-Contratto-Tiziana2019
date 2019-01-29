package DAO;

import Beans.ActiveContract;
import entity.OptionalService;
import entity.TypeOfPayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContractDao {
    private static ContractDao ourInstance  = null;

    public static synchronized ContractDao getInstance() {
        if (ourInstance == null) ourInstance = new ContractDao();
        return ourInstance;
    }

    private ContractDao() {
    }

    public ActiveContract getContract(int contractId){
        ActiveContract activeContract = null;
        String sql = "select  initDate, terminationDate, paymentMethod, tenantNickname, " +
                "renterNickname, grossPrice, netPrice\nfrom ActiveContract\n" +
                "where idContract = ?";
        try(Connection conn = C3poDataSource.getConnection()){
            //gestione transazione non in autocommit perché prevede piú di un'operazione
                conn.setAutoCommit(false);
                try(PreparedStatement st = conn.prepareStatement(sql)){
                    st.setInt(1, contractId);
                    ResultSet res = st.executeQuery();
                    if (res.next()){
                        activeContract = new ActiveContract(contractId,res.getDate("initDate").toLocalDate(),
                                res.getDate("terminationDate").toLocalDate(), TypeOfPayment.valueOf(res.getInt("paymentMethod")),
                                res.getString("tenantNickname"),res.getString("renterNickname"), res.getInt("grossPrice"),
                                res.getInt("netPrice"), getServices(contractId));
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return activeContract;
                }
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activeContract;
    }

    public List<OptionalService> getServices(int contractId){
        List<OptionalService> list = new ArrayList<>();
        String sql = "select idService, name, price,  description\n" +
                "from OptionalService\n" +
                "where ActiveContract_idContract = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, contractId);
            ResultSet res = st.executeQuery();
            while (res.next()){
                list.add(new OptionalService(res.getInt("idService"),res.getString("name"), res.getInt("price"),
                        res.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
