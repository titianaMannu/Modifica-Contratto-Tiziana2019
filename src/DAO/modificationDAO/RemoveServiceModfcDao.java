package DAO.modificationDAO;

import Beans.Contract;
import DAO.C3poDataSource;
import entity.OptionalService;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.RemoveServiceModification;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemoveServiceModfcDao implements RequestForModificationDao {

    private static RemoveServiceModfcDao ourInstance = null;


    private RemoveServiceModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static synchronized RemoveServiceModfcDao getInstance(){
        if (ourInstance == null)
            ourInstance = new RemoveServiceModfcDao();
        return ourInstance;
    }

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     * @return true se la transazione fa commit, false altrimenti
     */
    @Override
    public boolean updateContract(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException{
        if (request.getStatus() != RequestStatus.ACCEPTED)
            throw new IllegalStateException("Request is not accepted yet\n");
        //TODO spostare il controllo dello stato nel Control!!
        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be RemoveServiceModification");

        boolean commit = false;
        RemoveServiceModification modification = (RemoveServiceModification)request.getModification();
        OptionalService service = modification.getObjectToChange();
        String sql_1, sql_2;
        sql_1= "update OptionalService set ActiveContract_idContract = ?\n" +
                "where idService = ?";
        sql_2 = "update ActiveContract set grossPrice = grossPrice - ?\n" +
                "where idContract = ?";
        try (Connection conn = C3poDataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st1 = conn.prepareStatement(sql_1); PreparedStatement st2 = conn.prepareStatement(sql_2)) {

                st1.setInt(1, request.getContract().getContractId());
                st1.setInt(2, service.getServiceId());
                st2.setInt(1, service.getServicePrice());
                st2.setInt(2, request.getContract().getContractId());
                if (st1.executeUpdate() == 1 && st2.executeUpdate() == 1)
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
    public boolean insertModification( RequestForModification request)
            throws NullPointerException, IllegalArgumentException {
        Modification modification = request.getModification();
        if (!(modification instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be RemoveServiceModification\n");

        boolean commit = false;
        OptionalService service = (OptionalService) modification.getObjectToChange();
        String sql = "insert into RemoveServiceModification(requestId, requestC, service) values (?, ?, ?)";

        try (Connection conn = C3poDataSource.getConnection()){
            conn.setAutoCommit(false);
            try (PreparedStatement st = conn.prepareStatement(sql)) {

                st.setInt(1, request.getRequestId());
                st.setInt(2, request.getContract().getContractId());
                st.setInt(3, service.getServiceId());
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
        //todo spostare il controllo dello stato nel control!
        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument must be RemoveServiceModification\n");

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
            throws IllegalArgumentException, NullPointerException {
        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be RemoveServiceModification\n");

        RemoveServiceModification modification = (RemoveServiceModification)request.getModification();
        //prima controllo se avrebbe degli efetti sul contratto
        if (!modification.validate(request.getContract()))
            return false;

        OptionalService service = modification.getObjectToChange();
        String sql = "select name as serviceName, price as servicePrice\n" +
                "from RemoveServiceModification as m join requestForModification as rm on m.requestId = rm.idRequest " +
                "&& m.requestC = ?\n" +
                "join OptionalService OS on m.service = OS.idService\n" +
                "where rm.status = 0 ;" ;
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
            st.setInt(1, request.getContract().getContractId());
            ResultSet res = st.executeQuery();
            while(res.next())
                if (service.getServiceName().equals(res.getString("serviceName"))
                        && service.getServicePrice() == res.getInt("servicePrice"))
                    return false;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public Modification getModification(int contractId, int requestId) {
        String sql = "select name, price, description\n" +
                "from OptionalService as OS  join RemoveServiceModification ASM on OS.idService = ASM.service\n" +
                "where ASM.requestC = ? && ASM.requestId = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
            st.setInt(1, contractId);
            st.setInt(2, requestId);
            ResultSet res = st.executeQuery();
            if(res.next()){
                OptionalService service =new OptionalService(res.getString("name"), res.getString("description"),
                        res.getInt("price"));
                return ModificationFactory.getInstance().createProduct(service, TypeOfModification.REMOVE_SERVICE);
            }

        }catch(SQLException e){
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
            st.setInt(3, TypeOfModification.REMOVE_SERVICE.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo REMOVE_SERVICE  in questo caso
                Modification modfc = getModification(contract.getContractId(), res.getInt(3));
                RequestForModification request = new RequestForModification(contract, TypeOfModification.REMOVE_SERVICE,
                        modfc.getObjectToChange(),sender, res.getString(2), res.getDate(1).toLocalDate());
                //aggiunta della richiesta alla lista
                list.add(request);
            }
        }catch (SQLException | IllegalArgumentException e) {
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
                st.setInt(3, TypeOfModification.REMOVE_SERVICE.getValue());
                ResultSet res = st.executeQuery();
                while(res.next()){
                    //tipo di modifica è di tipo REMOVE_SERVICE  in questo caso
                    Modification modfc = getModification(contract.getContractId(), res.getInt(3));
                    RequestForModification request = new RequestForModification(contract, TypeOfModification.REMOVE_SERVICE,
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
            throws IllegalArgumentException, NullPointerException{
        boolean commit = false;
        if ( request.getStatus() != RequestStatus.PENDING )
            throw new IllegalStateException("You can insert a request if and only if this is PENDING\n");
        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument must be RemoveServiceModification\n");

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
                st.setInt(4, TypeOfModification.REMOVE_SERVICE.getValue());
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
