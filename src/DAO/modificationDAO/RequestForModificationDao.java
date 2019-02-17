package DAO.modificationDAO;

import DAO.ContractDao;
import entity.ActiveContract;
import beans.RequestBean;
import DAO.C3poDataSource;
import entity.modification.Modification;
import enumeration.TypeOfModification;
import entity.RequestForModification;
import enumeration.RequestStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class RequestForModificationDao {

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     */
    public abstract void updateContract(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException, SQLException;

    /**
     * inserisce la modifica nel DB
     */
    public abstract void insertModification(RequestForModification request)
            throws NullPointerException, IllegalArgumentException, IllegalStateException, SQLException;

    /**
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     */
    public abstract boolean validateRequest(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, SQLException;

    public abstract Modification getModification(int contractId, int requestId);

    /**
     * Si occupa dell'eliminazione della richiesta (politica di eliminazione a cascata per la modifica corrispondente)
    */
    public  void deleteRequest(RequestForModification request)
            throws IllegalStateException, NullPointerException, SQLException {

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");

        String sql = "delete\n" +
                "from requestForModification\n" +
                "where idRequest = ?";
        try (Connection conn = C3poDataSource.getConnection()) {
            conn.setAutoCommit(false);
            //altro blocco per poter gestire separatamente le situazioni di errore
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, request.getRequestId());
                if (st.executeUpdate() != 1)
                    throw new IllegalStateException("La richiesta da eliminare non è nel sistema\n");
            } catch (SQLException e) {
                // se l'operazione di tipo DML genera errore allora si deve riportare il DB in uno stato consistente
                conn.rollback();
                conn.setAutoCommit(true);
                throw e;
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     @return : una lista contenete tutte le proposte relative a contrat e fatte da sender
     */
    public abstract List<RequestBean> getRequests(ActiveContract activeContract, String sender);

    /**
     *@return una lista contenete tutte le richieste PENDING relative a contrat e destinate a receiver
     */
    public abstract List<RequestBean> getSubmits(int contractId,  String receiver);

    /**
     * Si occupa dell'inserimento della richiesta e della modifica corrispondente
     */
    public void insertRequest(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, SQLException, IllegalStateException {

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");

        String sql = "insert into requestForModification(contract, dateOfSubmission, reasonWhy, type, senderNickname,"+
                " receiverNickname)\nvalues (?, ?, ?, ?, ?, ?)";
        try(Connection conn = C3poDataSource.getConnection()){
            conn.setAutoCommit(false);
            //altro blocco per poter gestire separatamente le situazioni di errore
            try(PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                //preparazione dei dati
                st.setInt(1, request.getActiveContract().getContractId());
                st.setDate(2, Date.valueOf(request.getDateOfSubmission()));
                st.setString(3, request.getReasonWhy());
                st.setInt(4, request.getType().getValue());
                st.setString(5, request.getSenderNickname());
                st.setString(6, request.getReceiverNickname());
                //esecuzione dell'update
                if (st.executeUpdate() != 1)  throw new IllegalStateException("Non è stato possibile inserire la richiesta\n");
                ResultSet keys = st.getGeneratedKeys();
                if (keys.next()) //idService generato automaticamente
                    request.setRequestId(keys.getInt(1));

                insertModification(request);

            }catch (SQLException | IllegalArgumentException  e){
                // se l'operazione di tipo DML genera errore allora si deve riportare il DB in uno stato consistente
                conn.rollback();
                conn.setAutoCommit(true);
                throw e;
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public synchronized void setRequestStatus(RequestForModification request)
            throws  NullPointerException, SQLException, IllegalStateException {

        if (request == null ) throw new NullPointerException("Specificare una richiesta\n");

        String sql = "update requestForModification set status = ?\n" +
                "where idRequest = ?";
        try(Connection conn = C3poDataSource.getConnection();PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, request.getStatus().getValue());
            st.setInt(2, request.getRequestId());
            if (st.executeUpdate() != 1) throw new IllegalStateException("Non è stato possibile rendere la richiesta " +
                    request.getStatus().getDescription() + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * return una lista di richieste TO_EXIPIRE
     */
    public List<RequestForModification> getRequestsToExpire() {
            List<RequestForModification> list = new ArrayList<>();
            String before = "update requestForModification set status = ?\n" +
                    "where status = ?";
            String sql = "select  *\n" +
                    "from requestForModification\n" +
                    "where status = ?";
            try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql);
          PreparedStatement  beforeSt = conn.prepareStatement(before)) {
                beforeSt.setInt(1, RequestStatus.TO_EXPIRE.getValue());
                beforeSt.setInt(2, RequestStatus.PENDING.getValue());
                beforeSt.executeUpdate(); // tutte le richieste PENDING diventano TO_EXPIRE (motivi di sicurezza)

                st.setInt(1, RequestStatus.TO_EXPIRE.getValue());
                ResultSet res = st.executeQuery();
                while (res.next()) {
                    ActiveContract contract = ContractDao.getInstance().getContract(res.getInt("contract"));
                    if (contract == null) continue;
                    Modification modification = getModification(contract.getContractId(), res.getInt("idRequest"));
                    if (modification != null) {
                        try { //costruisco lista di richieste
                            RequestForModification request = new RequestForModification(res.getInt("idRequest"), contract,
                                    TypeOfModification.valueOf(res.getInt("type")), modification.getObjectToChange(),
                                    res.getString("senderNickname"), res.getString("reasonWhy"),
                                    res.getDate("dateOfSubmission").toLocalDate(), RequestStatus.valueOf(res.getInt("status")));
                            list.add(request);
                        }catch (IllegalArgumentException | NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }
            } catch (SQLException  e) {
                e.printStackTrace();
            }

            return list; //in caso di errori ritorna una lista parziale o vuota
        }



}
