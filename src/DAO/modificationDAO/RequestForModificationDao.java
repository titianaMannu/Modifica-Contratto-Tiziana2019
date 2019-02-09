package DAO.modificationDAO;

import entity.ActiveContract;
import Beans.RequestBean;
import DAO.C3poDataSource;
import entity.modification.Modification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;

import java.sql.*;
import java.util.List;

public abstract class RequestForModificationDao {

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     * todo test per ogni tipo
     */
    public abstract void updateContract(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException, SQLException;

    /**
     * inserisce la modifica nel DB
     */
    public abstract void insertModification(RequestForModification request) throws SQLException;

    /**
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     *todo test per ogni tipologia
     * @param request
     */
    public abstract boolean validateRequest(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, SQLException;

    public abstract Modification getModification(int contractId, int requestId);

    /**
     * Si occupa dell'eliminazione della richiesta (politica di eliminazione a cascata per la modifica corrispondente)
     * todo test
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
    public abstract List<RequestBean> getSubmits(ActiveContract activeContract, String receiver);

    /**
     * Si occupa dell'inserimento della richiesta e della modifica corrispondente
     * todo test
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


    public void changeRequestStatus(RequestForModification request, RequestStatus newStatus)
            throws  NullPointerException, SQLException, IllegalStateException {

        if (request == null ) throw new NullPointerException("Specificare una richiesta\n");

        String sql = "update requestForModification set status = ?\n" +
                "where idRequest = ?";
        try(Connection conn = C3poDataSource.getConnection();PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, newStatus.getValue());
            st.setInt(2, request.getRequestId());
            if (st.executeUpdate() != 1) throw new IllegalStateException("Non è stato possibile rendere la richiesta " +
                    newStatus.getDescription() + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }



}
