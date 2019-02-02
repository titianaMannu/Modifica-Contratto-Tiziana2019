package DAO;

import Beans.ActiveContract;
import Beans.OptionalService;
import entity.TypeOfPayment;
import entity.UserType;
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
        String sql = "select  stipulationDate, terminationDate, paymentMethod, tenantNickname, " +
                "renterNickname, netPrice, frequencyOfPayment\nfrom ActiveContract\n" +
                "where idContract = ?";
        try(Connection conn = C3poDataSource.getConnection()){
            //gestione transazione non in autocommit perché prevede piú di un'operazione
                conn.setAutoCommit(false);
                try(PreparedStatement st = conn.prepareStatement(sql)){
                    st.setInt(1, contractId);
                    ResultSet res = st.executeQuery();
                    if (res.next()){
                        activeContract = new ActiveContract(contractId,res.getDate("stipulationDate").toLocalDate(),
                                res.getDate("terminationDate").toLocalDate(), TypeOfPayment.valueOf(res.getInt("paymentMethod")),
                                res.getString("tenantNickname"),res.getString("renterNickname"),
                                res.getInt("netPrice"), getServices(contractId), res.getInt("frequencyOfPayment"));
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

    /**
     *ritorna una lista dei contratti attivi in base al tipo di user in input
     * @param userNickName : nickname dell'utente
     * @param type : renter o tenant, altrimenti viene ritornata una lista vuota
     */
    public List<ActiveContract> getAllActiveContracts(String userNickName, UserType type) {
        List<ActiveContract> list = new ArrayList<>();
        String sql;

        switch (type){
            case RENTER:
                sql = "select idContract\n" +
                        "from ActiveContract\n" +
                        "where renterNickname = ? ";
                break;
            case TENANT:
                sql = "select idContract\n" +
                        "from ActiveContract\n" +
                        "where tenantNickname = ? ";
                break;
                default: return list;
        }
        try (Connection connection = C3poDataSource.getConnection(); PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, userNickName);
            ResultSet res = st.executeQuery();
            while (res.next()){
                ActiveContract contract = getContract(res.getInt("idContract"));
                if (contract != null){
                    list.add(contract);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
