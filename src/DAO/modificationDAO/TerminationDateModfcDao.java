package DAO.modificationDAO;

import Beans.ActiveContract;
import Beans.RequestBean;
import DAO.C3poDataSource;
import entity.modification.*;
import entity.request.RequestForModification;
import entity.request.RequestStatus;

import javax.xml.bind.ValidationException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TerminationDateModfcDao extends RequestForModificationDao {

    private static TerminationDateModfcDao ourInstance = null;

    private TerminationDateModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static synchronized TerminationDateModfcDao getInstance(){
        if (ourInstance == null)
            ourInstance = new TerminationDateModfcDao();
        return ourInstance;
    }

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     */
    @Override
    public void updateContract(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException, SQLException  {

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");
        if (! (request.getModification() instanceof TerminationDateModification))
            throw new IllegalArgumentException("Argument had to be TerminationDateModification");

        TerminationDateModification modification = (TerminationDateModification) request.getModification();
        LocalDate date = modification.getObjectToChange();
        String sql ="update ActiveContract set terminationDate = ?\n" +
                "where idContract = ?";

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
                st.setDate(1, Date.valueOf(date));
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
        if (! (modification instanceof TerminationDateModification))
            throw new IllegalArgumentException("Argomento della richiesta deve essere di tipo TerminationDateModification");

        String sql = "insert into TerminationDateModification(requestId, requestC, newDate) values (?, ?, ?)" ;
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql) ){
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
            LocalDate date = (LocalDate)modification.getObjectToChange();
            st.setInt(1, request.getRequestId());
            st.setInt(2, request.getActiveContract().getContractId());
            st.setDate(3, Date.valueOf(date));
            throw new IllegalStateException("Non è possibile inserire la modifica: " +
                    "controlla lo stato della richiesta\n");
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     */
    @Override
    public void validateRequest(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, ValidationException, SQLException{

        if (request == null ) throw new NullPointerException("Specificare una richiesta\n");
        if (!(request.getModification() instanceof TerminationDateModification)) {
            throw new IllegalArgumentException();
        }

        TerminationDateModification modification = (TerminationDateModification) request.getModification();
        //prima controllo se avrebbe degli efetti sul contratto
        if (!modification.validate(request.getActiveContract()))
            throw new ValidationException("Specificare una modifica significativa\n");

        String sql = "select count(newDate) as numOfRequests\n" +
                "from TerminationDateModification as m join requestForModification as rm on m.requestId = rm.idRequest" +
                " && m.requestC = ?\nwhere rm.status = 0" ;

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit())
                conn.setAutoCommit(true);
            st.setInt(1, request.getActiveContract().getContractId());
            ResultSet res = st.executeQuery();
            if (res.next()) {
                if (res.getInt("numOfRequests") > 0)
                    throw new ValidationException("Esiste già una richiesta di modifica analoga per il contratto\n");
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Modification getModification(int contractId, int requestId) {
        String sql = "select newDate\n" +
                "from TerminationDateModification\n" +
                "where requestId = ? && requestC = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, requestId);
            st.setInt(2, contractId);
            ResultSet res = st.executeQuery();
            if (res.next())
                return ModificationFactory.getInstance().createProduct(res.getDate(1).toLocalDate(),
                        TypeOfModification.CHANGE_TERMINATIONDATE);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     @return : una lista contenete tutte le richieste  relative contrat e inviate da sender
     */
    @Override
    public List<RequestBean> getRequests(ActiveContract activeContract, String sender) throws NullPointerException {
        if (activeContract == null || sender == null || sender.isEmpty())
            throw new NullPointerException("Specificare il contratto e il mittente\n");

        List<RequestBean> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest, status\n" +
                "from requestForModification\n" +
                "where  (contract, senderNickname, type) in ((?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, activeContract.getContractId());
            st.setString(2, sender);
            st.setInt(3, TypeOfModification.CHANGE_TERMINATIONDATE.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo CHANGE_PAYMENTMETHOD in questo caso
                Modification modfc = getModification(activeContract.getContractId(), res.getInt("idRequest"));
                if (modfc == null)
                    throw new NullPointerException("modifica non trovata\n");

                RequestBean request = new RequestBean(TypeOfModification.CHANGE_TERMINATIONDATE,
                        modfc.getObjectToChange(), res.getString("reasonWhy"), res.getDate("dateOfSubmission").toLocalDate(),
                        RequestStatus.valueOf(res.getInt("status")), res.getInt("idRequest"), sender);
                list.add(request);
            }
        }catch (SQLException | IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }
        //ritorno ugualmente la lista
        return list;
    }

    /**
     *@return una lista contenete tutte le richieste  relative contrat e destinate a receiver
     */
    @Override
    public List<RequestBean> getSubmits(ActiveContract activeContract, String receiver) {
        List<RequestBean> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest, senderNickname\n" +
                "from requestForModification\n" +
                "where  (contract, receiverNickname, type, status) in ((?, ?, ?,?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, activeContract.getContractId());
            st.setString(2, receiver);
            st.setInt(3, TypeOfModification.CHANGE_TERMINATIONDATE.getValue());
            st.setInt(4, RequestStatus.PENDING.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo REMOVE_SERVICE  in questo caso
                Modification modfc = getModification(activeContract.getContractId(), res.getInt("idRequest"));
                if (modfc == null)
                    throw new NullPointerException("modifica non trovata\n");

                RequestBean request = new RequestBean(TypeOfModification.CHANGE_TERMINATIONDATE,
                        modfc.getObjectToChange(), res.getString("reasonWhy"), res.getDate("dateOfSubmission").toLocalDate(),
                        RequestStatus.PENDING, res.getInt("idRequest"), res.getString("senderNickname"));
                list.add(request);
            }
        }catch (SQLException  e) {
            e.printStackTrace();
        }
        //ritorno ugualmente la lista
        return list;
    }

}
