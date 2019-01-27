package DAO.modificationDAO;

import Beans.ActiveContract;
import Beans.RequestBean;
import DAO.C3poDataSource;
import Beans.OptionalService;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.RemoveServiceModification;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;

import javax.xml.bind.ValidationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemoveServiceModfcDao extends RequestForModificationDao {

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
     */
    @Override
    public void updateContract(RequestForModification request)
            throws IllegalStateException, IllegalArgumentException, NullPointerException, SQLException {

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");

        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argomento deve essere di tipo RemoveServiceModification");

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

                st1.setInt(1, request.getActiveContract().getContractId());
                st1.setInt(2, service.getServiceId());
                st2.setInt(1, service.getServicePrice());
                st2.setInt(2, request.getActiveContract().getContractId());
                if (!(st1.executeUpdate() == 1 && st2.executeUpdate() == 1))
                    throw new IllegalStateException("Non è possibile effettuare la modifica: " +
                            "controlla lo stato del contratto\n");

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
            throws NullPointerException, IllegalArgumentException, SQLException {

        if (request == null) throw new NullPointerException("Specificare una richiesta\n");

        Modification modification = request.getModification();
        if (!(modification instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argomento deve essere di tipo RemoveServiceModification\n");

        OptionalService service = (OptionalService) modification.getObjectToChange();
        String sql = "insert into RemoveServiceModification(requestId, requestC, service) values (?, ?, ?)";
        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
            //operazione singola non necessita di fare rallback in caso di errore
            st.setInt(1, request.getRequestId());
            st.setInt(2, request.getActiveContract().getContractId());
            st.setInt(3, service.getServiceId());
            if (st.executeUpdate() != 1) //già esiste questa modifica
                throw new IllegalStateException("Non è possibile inserire la modifica: " +
                        "controlla lo stato della richiesta\n");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     */
    @Override
    public void validateRequest(RequestForModification request)
            throws IllegalArgumentException, NullPointerException, ValidationException, SQLException {

        if (request == null ) throw new NullPointerException("Specificare una richiesta\n");

        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argomento della richiesta deve essere di tipo RemoveServiceModification\n");

        RemoveServiceModification modification = (RemoveServiceModification)request.getModification();
        //prima controllo se avrebbe degli efetti sul contratto
        if (!modification.validate(request.getActiveContract()))
             throw new ValidationException("Specificare una modifica significativa\n");

        OptionalService service = modification.getObjectToChange();
        String sql = "select name as serviceName, price as servicePrice\n" +
                "from RemoveServiceModification as m join requestForModification as rm on m.requestId = rm.idRequest " +
                "&& m.requestC = ?\n" +
                "join OptionalService OS on m.service = OS.idService\n" +
                "where rm.status = 0 ;" ;
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
            st.setInt(1, request.getActiveContract().getContractId());
            ResultSet res = st.executeQuery();
            while(res.next())
                if (service.getServiceName().equals(res.getString("serviceName"))
                        && service.getServicePrice() == res.getInt("servicePrice"))
                    throw new ValidationException("Esiste già questa richiesta di modifica per il contratto\n");
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param contractId
     * @param requestId
     * @return la modifica corrispondente alla PK(contractId, requestId) se la query ha successo, null altrimenti
     */
    @Override
    public Modification getModification(int contractId, int requestId) {
        String sql = "select idService, name, price, description\n" +
                "from OptionalService as OS  join RemoveServiceModification ASM on OS.idService = ASM.service\n" +
                "where ASM.requestC = ? && ASM.requestId = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            if(!conn.getAutoCommit())
                conn.setAutoCommit(true);
            st.setInt(1, contractId);
            st.setInt(2, requestId);
            ResultSet res = st.executeQuery();
            if(res.next()){
                OptionalService service =new OptionalService(res.getInt("idService"), res.getString("name"),
                        res.getInt("price"),res.getString("description"));
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
            st.setInt(3, TypeOfModification.REMOVE_SERVICE.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo REMOVE_SERVICE  in questo caso
                Modification modfc = getModification(activeContract.getContractId(), res.getInt("idRequest"));
                if (modfc == null)
                    throw new NullPointerException("modifica non trovata\n");

                RequestBean request = new RequestBean(TypeOfModification.REMOVE_SERVICE,
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
     *@return una lista contenete tutte le richieste PENDING relative contrat e destinate a receiver
     */
    @Override
    public List<RequestBean> getSubmits(ActiveContract activeContract, String receiver) throws NullPointerException {
            if (activeContract == null || receiver == null || receiver.isEmpty())
                throw new NullPointerException("Specificare il contratto e il destinatario\n");

            List<RequestBean> list = new ArrayList<>();
            String sql = "select  idRequest, dateOfSubmission, reasonWhy, senderNickname\n" +
                    "from requestForModification\n" +
                    "where  (contract, receiverNickname, type, status) in ((?, ?, ?, ?))";
            try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
                if (!conn.getAutoCommit() )
                    conn.setAutoCommit(true);
                st.setInt(1, activeContract.getContractId());
                st.setString(2, receiver);
                st.setInt(3, TypeOfModification.REMOVE_SERVICE.getValue());
                st.setInt(4, RequestStatus.PENDING.getValue());
                ResultSet res = st.executeQuery();
                while(res.next()){
                    //tipo di modifica è di tipo REMOVE_SERVICE  in questo caso
                    Modification modfc = getModification(activeContract.getContractId(), res.getInt("idRequest"));
                    if (modfc == null)
                        throw new NullPointerException("tipo di richiesta e modifica non compatibili\n");

                    RequestBean request = new RequestBean(TypeOfModification.REMOVE_SERVICE,
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
