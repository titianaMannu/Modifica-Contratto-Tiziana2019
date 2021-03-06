package DAO.modificationDAO;

import entity.ActiveContract;
import beans.RequestBean;
import DAO.C3poDataSource;
import enumeration.TypeOfPayment;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.PaymentMethodModification;
import enumeration.TypeOfModification;
import entity.RequestForModification;
import enumeration.RequestStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodModfcDao extends RequestForModificationDao {

    private PaymentMethodModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    private static class LazyContainer{
        private static final PaymentMethodModfcDao instance = new PaymentMethodModfcDao();
    }

    public static PaymentMethodModfcDao getInstance(){
        return  LazyContainer.instance;
    }

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     */
    @Override
    public void updateContract(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException, SQLException  {

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");
        if (! (request.getModification() instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument had to be PaymentMethoModification");

        String sql ="update ActiveContract set paymentMethod = ?\n" +
                "where idContract = ?";
        PaymentMethodModification modification = (PaymentMethodModification) request.getModification();
        TypeOfPayment type = modification.getObjectToChange();
        try (Connection conn = C3poDataSource.getConnection();PreparedStatement st = conn.prepareStatement(sql)) {
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
                st.setInt(1, type.getValue());
                st.setInt(2, request.getActiveContract().getContractId());
                if (st.executeUpdate() != 1)  throw new IllegalStateException("Non è possibile effettuare la modifica:" +
                        " controlla lo stato del contratto\n");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * inserisce la modifica nel DB
     */
    @Override
    public void insertModification(RequestForModification request)
            throws NullPointerException, IllegalArgumentException, SQLException,IllegalStateException {

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");
        Modification modification = request.getModification();
        if (! (modification instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argomento della richiesta deve essere di tipo PaymentMethodModification\n");

        String sql = "insert into PaymentMethodModification(requestId, requestC, paymentMethod) values (?, ?, ?)" ;
        try(Connection conn = C3poDataSource.getConnection(); Statement stmt = conn.createStatement()) {
            if (!conn.getAutoCommit())
                conn.setAutoCommit(true);
            stmt.execute("SET FOREIGN_KEY_CHECKS=0"); //disabilito il controllo delle FK in fase di inserimento
            try(PreparedStatement st = conn.prepareStatement(sql)) {
                TypeOfPayment type = (TypeOfPayment) modification.getObjectToChange();
                st.setInt(1, request.getRequestId());
                st.setInt(2, request.getActiveContract().getContractId());
                st.setInt(3, type.getValue());
                if (st.executeUpdate() != 1) //già esiste questa modifica
                    throw new IllegalStateException("Non è possibile inserire la modifica: " +
                            "controlla lo stato della richiesta\n");
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }finally {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1"); //riabilito il controllo delle FK
            }
        }
    }


    /**
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     * @param request
     */
    @Override
    public boolean validateRequest(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, SQLException{

        if (request == null ) throw new NullPointerException("Specificare una richiesta\n");
        if (!(request.getModification() instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argomento della richiesta deve essere di tipo PaymentMethodModification\n");

        String sql = "select count(paymentMethod) as numOfRequests\n" +
                "from PaymentMethodModification as m join requestForModification as rm on m.requestId = rm.idRequest" +
                " && m.requestC = ?  where rm.status = ?  or rm.status = ?";
        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, request.getActiveContract().getContractId());
            st.setInt(2, RequestStatus.PENDING.getValue());
            st.setInt(3, RequestStatus.TO_EXPIRE.getValue());
            ResultSet res = st.executeQuery();
            if (res.next()) {
                if (res.getInt("numOfRequests") > 0)
                    return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        return true;
    }


    @Override
    public Modification getModification(int contractId, int requestId) {
        String sql = "select paymentMethod\n" +
                "from PaymentMethodModification\n" +
                "where requestId = ? && requestC = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, requestId);
            st.setInt(2, contractId);
            ResultSet res = st.executeQuery();
            if (res.next())
                return ModificationFactory.getInstance().createProduct(TypeOfPayment.valueOf(res.getInt(1)),
                        TypeOfModification.CHANGE_PAYMENTMETHOD);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     @return : una lista contenete tutte le richieste  relative contrat e inviate da sender
     */
    @Override
    public List<RequestBean> getRequests(ActiveContract activeContract, String sender) {
        if (activeContract == null || sender == null || sender.isEmpty())
            throw new NullPointerException("Specificare il contratto e il mittente\n");

        List<RequestBean> list = new ArrayList<>();
        String sql = "select  idRequest, dateOfSubmission, reasonWhy, idRequest, status\n" +
                "from requestForModification\n" +
                "where  (contract, senderNickname, type) in ((?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, activeContract.getContractId());
            st.setString(2, sender);
            st.setInt(3, TypeOfModification.CHANGE_PAYMENTMETHOD.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo CHANGE_PAYMENTMETHOD in questo caso
                Modification modfc = getModification(activeContract.getContractId(), res.getInt("idRequest"));
                if (modfc == null)
                    continue;

                RequestBean request = new RequestBean(TypeOfModification.CHANGE_PAYMENTMETHOD,
                        modfc.getObjectToChange(), res.getString("reasonWhy"), res.getDate("dateOfSubmission").toLocalDate(),
                        RequestStatus.valueOf(res.getInt("status")), res.getInt("idRequest"), sender);
                //aggiunta della richiesta alla lista
                list.add(request);
            }
        }catch (SQLException  e) {
            e.printStackTrace();
        }
        //ritorno ugualmente la lista
        return list;
    }

    /**
     *@return una lista contenete tutte le richieste  relative contrat e destinate a receiver
     */
    @Override
    public List<RequestBean> getSubmits(int contractId, String receiver) {
        if (contractId < 1 || receiver == null || receiver.isEmpty())
            throw new NullPointerException("Specificare il contratto e il destinatario\n");

        List<RequestBean> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest, senderNickname\n" +
                "from requestForModification\n" +
                "where  (contract, receiverNickname, type, status) in ((?, ?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, contractId);
            st.setString(2, receiver);
            st.setInt(3, TypeOfModification.CHANGE_PAYMENTMETHOD.getValue());
            st.setInt(4, RequestStatus.PENDING.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo REMOVE_SERVICE  in questo caso
                Modification modfc = getModification(contractId, res.getInt("idRequest"));
                if (modfc == null)
                    continue;
                RequestBean request = new RequestBean(TypeOfModification.CHANGE_PAYMENTMETHOD,
                        modfc.getObjectToChange(), res.getString("reasonWhy"), res.getDate("dateOfSubmission").toLocalDate(),
                        RequestStatus.PENDING, res.getInt("idRequest"), res.getString("senderNickname"));
                //aggiunta della richiesta alla lista
                list.add(request);
            }
        }catch (SQLException  e) {
            e.printStackTrace();
        }
        //ritorno ugualmente la lista
        return list;

    }
}
