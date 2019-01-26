package DAO.modificationDAO;

import Beans.Contract;
import DAO.C3poDataSource;
import entity.TypeOfPayment;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.PaymentMethodModification;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodModfcDao implements RequestForModificationDao {

    private static PaymentMethodModfcDao ourInstance = null;

    private PaymentMethodModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static synchronized PaymentMethodModfcDao getInstance(){
        if (ourInstance == null)
            ourInstance = new PaymentMethodModfcDao();
        return ourInstance;
    }

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     * @return true se la transazione fa commit, false altrimenti
     */
    @Override
    public boolean updateContract(RequestForModification request)
            throws  IllegalStateException,IllegalArgumentException, NullPointerException {
        if (request.getStatus() != RequestStatus.ACCEPTED)
            throw new IllegalStateException("Request is not accepted yet\n");
        //todo controllo stato nel control
        if (! (request.getModification() instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument had to be PaymentMethoModification");

        String sql ="update ActiveContract set paymentMethod = ?\n" +
                "where idContract = ?";

        boolean commit = false;
        PaymentMethodModification modification = (PaymentMethodModification) request.getModification();
        TypeOfPayment type = modification.getObjectToChange();
        try (Connection conn = C3poDataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, type.getValue());
                st.setInt(2, request.getContract().getContractId());
                if (st.executeUpdate() == 1)
                    commit = true;

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                return commit;
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commit;
    }

    /**
     * inserisce la modifica nel DB
     */
    @Override
    public boolean insertModification(RequestForModification request)
            throws IllegalArgumentException, NullPointerException{
        Modification modification = request.getModification();
        if (! (modification instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument had to be PaymentMethodModification\n");

        String sql = "insert into PaymentMethodModification(requestId, requestC, paymentMethod) values (?, ?, ?)" ;
        boolean commit = false;
        try(Connection conn = C3poDataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                TypeOfPayment type = (TypeOfPayment) modification.getObjectToChange();
                st.setInt(1, request.getRequestId());
                st.setInt(2, request.getContract().getContractId());
                st.setInt(3, type.getValue());
                if (st.executeUpdate() == 1)
                    commit = true;

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                return commit;
            }
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commit;
    }

    /**
     * Si occupa dell'eliminazione della richiesta (politica di eliminazione a cascata per la modifica corrispondente)
     * @param request : richiesta da modificare
     * @return true se la transazione si è conclusa con successo, false altrimenti
     */
    @Override
    public boolean deleteRequest(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException {
        boolean commit = false;
        if ( request.getStatus() != RequestStatus.CLOSED )
            throw new IllegalStateException("You can delete a request if and only if this is CLOSED\n");
        //todo controllo dello stato nel control
        if (! (request.getModification() instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument must be PaymentMethodModification\n");

        String sql = "delete\n" +
                "from requestForModification\n" +
                "where idRequest = ?";
        try(Connection conn = C3poDataSource.getConnection()){
            conn.setAutoCommit(false);
            //altro blocco per poter gestire separatamente le situazioni di errore
            try(PreparedStatement st = conn.prepareStatement(sql)){
                st.setInt(1, request.getRequestId());
                if (st.executeUpdate() == 1) commit = true;
            }catch (SQLException e){
                // se l'operazione di tipo DML genera errore allora si deve riportare il DB in uno stato consistente
                conn.rollback();
                conn.setAutoCommit(true);
                return commit;
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commit;
    }

    /**
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     * @return true se la  richiesta di modifica è applicabile, false altrimenti
     */
    @Override
    public boolean validateModification(RequestForModification request)
            throws IllegalArgumentException, NullPointerException{
        if (!(request.getModification() instanceof PaymentMethodModification))
            throw new IllegalArgumentException();

        PaymentMethodModification modification = (PaymentMethodModification) request.getModification();
        //prima controllo se avrebbe degli efetti sul contratto
        if(!modification.validate(request.getContract()))
            return false;

        String sql = "select count(paymentMethod) as numOfRequests\n" +
                "from PaymentMethodModification as m join requestForModification as rm on m.requestId = rm.idRequest" +
                " && m.requestC = ?  where rm.status = 0";

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, request.getContract().getContractId());
            ResultSet res = st.executeQuery();
            if (res.next())
                return res.getInt("numOfRequests") == 0;
        }catch (SQLException e){
            e.printStackTrace();
        }

        //se arrivo qui qualcosa è andato storto
        return false;
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
    public List<RequestForModification> getSubmits(Contract contract, String sender) {
        List<RequestForModification> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest\n" +
                "from requestForModification\n" +
                "where  (contract, senderNickname, type) in ((?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, contract.getContractId());
            st.setString(2, sender);
            st.setInt(3, TypeOfModification.CHANGE_PAYMENTMETHOD.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo CHANGE_PAYMENTMETHOD in questo caso
                Modification modfc = getModification(contract.getContractId(), res.getInt(3));
                RequestForModification request = new RequestForModification(contract, TypeOfModification.CHANGE_PAYMENTMETHOD,
                        modfc.getObjectToChange(),sender, res.getString(2), res.getDate(1).toLocalDate());
                //aggiunta della richiesta alla lista
                list.add(request);
            }
        }catch (SQLException | IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            //se ho un'eccezione quello che si vuole è ritornare i dati però avere coscienza che c'è stato un errore
            //allora ritorno una lista truncate che termina con null che in questo tipo di lista non dovrebbe esserci
            list.add(null);
        }
        finally {
            //ritorno ugualmente la lista
            return list;
        }
    }

    /**
     *@return una lista contenete tutte le richieste  relative contrat e destinate a receiver
     */
    @Override
    public List<RequestForModification> getRequests(Contract contract, String receiver) {
        List<RequestForModification> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest, senderNickname\n" +
                "from requestForModification\n" +
                "where  (contract, receiverNickname, type) in ((?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, contract.getContractId());
            st.setString(2, receiver);
            st.setInt(3, TypeOfModification.CHANGE_PAYMENTMETHOD.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo CHANGE_PAYMENTMETHOD in questo caso
                Modification modfc = getModification(contract.getContractId(), res.getInt(3));
                RequestForModification request = new RequestForModification(contract, TypeOfModification.CHANGE_PAYMENTMETHOD,
                        modfc.getObjectToChange(),res.getString(4), res.getString(2), res.getDate(1).toLocalDate());
                //aggiunta della richiesta alla lista
                list.add(request);
            }
        }catch (SQLException | IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            //se ho un'eccezione quello che si vuole è ritornare i dati però avere coscienza che c'è stato un errore
            //allora ritorno una lista truncate che termina con null che in questo tipo di lista non dovrebbe esserci
            list.add(null);
        }
        finally {
            //ritorno ugualmente la lista
            return list;
        }
    }

    /**
     * Si occupa dell'inserimento della richiesta e della modifica corrispondente
     * @return : true se la transazione si conclude con successo, false altrimenti
     */
    @Override
    public boolean insertRequests(RequestForModification request)
            throws  IllegalArgumentException, NullPointerException{
        boolean commit = false;
        if ( request.getStatus() != RequestStatus.PENDING )
            throw new IllegalStateException("You can insert a request if and only if this is PENDING\n");

        if (! (request.getModification() instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument must be PaymentMethodModification\n");

        String sql = "insert into requestForModification(contract, dateOfSubmission, reasonWhy, type, senderNickname,"+
                " receiverNickname)\nvalues (?, ?, ?, ?, ?, ?)";
        try(Connection conn = C3poDataSource.getConnection()){
            conn.setAutoCommit(false);
            //altro blocco per poter gestire separatamente le situazioni di errore
            try(PreparedStatement st = conn.prepareStatement(sql)){
                //preparazione dei dati
                st.setInt(1, request.getRequestId());
                st.setDate(2, Date.valueOf(request.getDateOfSubmission()));
                st.setString(3, request.getReasonWhy());
                st.setInt(4, TypeOfModification.CHANGE_PAYMENTMETHOD.getValue());
                st.setString(5, request.getSenderNickname());
                st.setString(6, request.getReceiverNickname());
                //esecuzione dell'update
                if (st.executeUpdate() == 1 && insertModification(request)) commit = true;

            }catch (SQLException e){
                // se l'operazione di tipo DML genera errore allora si deve riportare il DB in uno stato consistente
                conn.rollback();
                conn.setAutoCommit(true);
                return commit;
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commit;
    }
}
