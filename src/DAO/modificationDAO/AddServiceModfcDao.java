package DAO.modificationDAO;

import Beans.ActiveContract;
import Beans.RequestBean;
import DAO.C3poDataSource;
import Beans.OptionalService;
import entity.modification.AddServiceModification;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AddServiceModfcDao extends RequestForModificationDao {

    private static AddServiceModfcDao ourInstance = null;

    private AddServiceModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static synchronized AddServiceModfcDao getInstance(){
        if (ourInstance == null )
            ourInstance = new AddServiceModfcDao();
        return ourInstance;
    }

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     */
    @Override
    public void updateContract(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException, SQLException{

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");

        if (! (request.getModification() instanceof AddServiceModification))
            throw new IllegalArgumentException("Argomento deve essere di tipo AddServiceModification\n");

        AddServiceModification modification = (AddServiceModification)request.getModification();
        OptionalService service = modification.getObjectToChange();
        String sql_1, sql_2;
        sql_1= "update OptionalService set ActiveContract_idContract = ?\n" +
                "where idService = ?";
        sql_2 = "update ActiveContract set grossPrice = grossPrice + ?\n" +
                "where idContract = ?";
        try (Connection conn = C3poDataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st1 = conn.prepareStatement(sql_1); PreparedStatement st2 = conn.prepareStatement(sql_2)) {
                st1.setInt(1, request.getActiveContract().getContractId());
                st1.setInt(2, service.getServiceId());
                st2.setInt(1, service.getServicePrice());
                st2.setInt(2, request.getActiveContract().getContractId());
                if (!(st1.executeUpdate() == 1 && st2.executeUpdate() == 1))
                    throw new IllegalStateException("Non è possibile effettuare la modifica\n");
            } catch (SQLException | IllegalStateException e) {
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
     * inserisce la modifica nel DB
     */
    @Override
    public void insertModification(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, SQLException, IllegalStateException{

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");

        Modification modification = request.getModification();
        if (!(modification instanceof AddServiceModification))
            throw new IllegalArgumentException("Argomento deve essere di tipo  AddServiceModificationn\n");

        OptionalService service = (OptionalService) modification.getObjectToChange();
        String sql_1 = "insert into AddServiceModification(requestId, requestC, service) values (?, ?, ?)";
        String sql_2 = "insert into OptionalService(name, price, description) values (?, ?, ?)";
        try (Connection conn = C3poDataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st_1 = conn.prepareStatement(sql_1);
                 PreparedStatement st_2 = conn.prepareStatement(sql_2, Statement.RETURN_GENERATED_KEYS);
                 Statement stmt = conn.createStatement()) {

                stmt.execute("SET FOREIGN_KEY_CHECKS=0"); //disabilito il controllo delle FK in fase di inserimento

                //inserimento del servizio
                st_2.setString(1, service.getServiceName());
                st_2.setInt(2, service.getServicePrice());
                st_2.setString(3, service.getDescription());
                //dopo l'inserimento deve risultare il cambiamento di 1 riga
                st_2.executeUpdate();
                ResultSet keys = st_2.getGeneratedKeys();
                if (keys.next()) //idService generato automaticamente
                    service.setServiceId(keys.getInt(1));
                else throw new IllegalStateException("c'è stato un errore durante l'inserimento dell'oggetto" +
                        " della modifica richiesta\n");

                //inserimento della modifica stessa
                st_1.setInt(1, request.getRequestId());
                st_1.setInt(2, request.getActiveContract().getContractId());
                st_1.setInt(3, service.getServiceId());
                //dopo l'inserimento deve risultare il cambiamento di 1 riga
                if (st_1.executeUpdate() != 1)
                        throw new IllegalStateException("Non è possibile inserire la modifica: " +
                              "controlla lo stato della richiesta\n");

                stmt.execute("SET FOREIGN_KEY_CHECKS=1"); //riabilito il controllo delle FK

            } catch (SQLException | IllegalStateException e) {
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
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     * @param request
     */
    @Override
    public boolean validateRequest(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, SQLException {

        if (request == null ) throw new NullPointerException("Specificare una richiesta\n");
        if (! (request.getModification() instanceof AddServiceModification))
            throw new IllegalArgumentException("Argomento deve essere di tipo AddServiceModification\n");

        AddServiceModification modification = (AddServiceModification)request.getModification();
        OptionalService service =  modification.getObjectToChange();
        String sql = "select name as serviceName, price as servicePrice\n" +
                "from AddServiceModification as m join requestForModification as rm on m.requestId = rm.idRequest " +
                "&& m.requestC = ?\njoin OptionalService OS on m.service = OS.idService\n" +
                "where rm.status = 0" ;
        try (Connection  conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit())
                conn.setAutoCommit(true);
            st.setInt(1, request.getActiveContract().getContractId());
            ResultSet res = st.executeQuery();
            while(res.next())
                if (service.getServiceName().equals(res.getString("serviceName"))
                        && service.getServicePrice() == res.getInt("servicePrice")){
                   return false;
                }
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    /**
     * @param contractId
     * @param requestId
     * @return la modifica corrispondente alla PK(contractId, requestId) se la query ha successo, null altrimenti
     */
    @Override
    public Modification getModification(int contractId, int requestId) {
        String sql = "select idService,name, price, description\n" +
                "from OptionalService as OS  join AddServiceModification ASM on OS.idService = ASM.service\n" +
                "where ASM.requestC = ? && ASM.requestId = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit())
                conn.setAutoCommit(true);
            st.setInt(1, contractId);
            st.setInt(2, requestId);
            ResultSet res = st.executeQuery();
            if(res.next()){
                OptionalService service =new OptionalService(res.getInt("idService"), res.getString("name"),
                        res.getInt("price"),res.getString("description"));
                return ModificationFactory.getInstance().createProduct(service, TypeOfModification.ADD_SERVICE);
            }
        }catch(SQLException e){
           e.printStackTrace();
        }
        return null;
    }

    /**
     * ritorna una lista di tutte le richieste di tipo ADD_SERVICE corrispondenti alla coppia (activeContract, sender)
     * Viene ritornato un elenco completo indipendentemente dallo stato
     */
    @Override
    public List<RequestBean> getRequests(ActiveContract activeContract, String sender)throws NullPointerException{
        if (activeContract == null || sender == null || sender.isEmpty())
            throw new NullPointerException("Specificare il contratto e il mittente\n");

        List<RequestBean> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest, status\n" +
                "from requestForModification\n" +
                "where  (contract, senderNickname, type) in ((?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit())
                conn.setAutoCommit(true);

            st.setInt(1, activeContract.getContractId());
            st.setString(2, sender);
            st.setInt(3, TypeOfModification.ADD_SERVICE.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo addService in questo caso
                Modification modfc = getModification(activeContract.getContractId(), res.getInt("idRequest"));
                if (modfc == null)
                    throw new NullPointerException("modifica non trovata\n");

                //creo la richiesta
                RequestBean request = new RequestBean(TypeOfModification.ADD_SERVICE,
                        modfc.getObjectToChange(), res.getString("reasonWhy"), res.getDate("dateOfSubmission").toLocalDate(),
                        RequestStatus.valueOf(res.getInt("status")), res.getInt("idRequest"), sender);
                //aggiunta della richiesta alla lista
                list.add(request);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        //ritorno ugualmente la lista
        return list;
    }

    /**
     * La lista ritornata contiene richiieste di tipo per modifiche di tipo ADD_SERVICE
     *  @return una lista contenete tutte le richieste  relative contrat e destinate a receiver
     */
    @Override
    public List<RequestBean> getSubmits(ActiveContract activeContract, String receiver) throws NullPointerException{
        if (activeContract == null || receiver == null || receiver.isEmpty())
            throw new NullPointerException("Specificare il contratto e il destinatario\n");

        List<RequestBean> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest, senderNickname\n" +
                "from requestForModification\n" +
                "where  (contract, receiverNickname, type, status) in ((?, ?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if (!conn.getAutoCommit() )
                conn.setAutoCommit(true);
            st.setInt(1, activeContract.getContractId());
            st.setString(2, receiver);
            st.setInt(3, TypeOfModification.ADD_SERVICE.getValue());
            st.setInt(4, RequestStatus.PENDING.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo addService in questo caso
                Modification modfc = getModification(activeContract.getContractId(), res.getInt("idRequest"));
                if (modfc == null)
                    throw new NullPointerException("modifica non trovata\n");
                //creo la richiesta
                RequestBean request = new RequestBean(TypeOfModification.ADD_SERVICE,
                        modfc.getObjectToChange(), res.getString("reasonWhy"), res.getDate("dateOfSubmission").toLocalDate(),
                        RequestStatus.PENDING, res.getInt("idRequest"), res.getString("senderNickname"));
                //aggiunta della richiesta alla lista
                list.add(request);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        //ritorno ugualmente la lista
        return list;

    }

}
