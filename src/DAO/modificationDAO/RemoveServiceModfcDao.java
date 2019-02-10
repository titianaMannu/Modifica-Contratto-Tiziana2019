package DAO.modificationDAO;

import entity.ActiveContract;
import beans.RequestBean;
import DAO.C3poDataSource;
//import beans.OptionalServiceBean;
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
        sql_1= "update OptionalService set ActiveContract_idContract = -1 where idService = ?";
        sql_2 = "update ActiveContract set grossPrice = grossPrice - ?\n" +
                "where idContract = ?";
        try (Connection conn = C3poDataSource.getConnection(); Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            stmt.execute("SET FOREIGN_KEY_CHECKS=0");
            try (PreparedStatement st1 = conn.prepareStatement(sql_1); PreparedStatement st2 = conn.prepareStatement(sql_2)) {
                st1.setInt(1 , service.getServiceId());
                st2.setInt(1, service.getServicePrice());
                st2.setInt(2, request.getActiveContract().getContractId());
                if (!(st1.executeUpdate() == 1 && st2.executeUpdate() == 1))
                    throw new IllegalStateException("Non è possibile effettuare la modifica: " +
                            "controlla lo stato del contratto\n");

            } catch (SQLException | IllegalStateException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw e;
            }finally {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
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
        try (Connection conn = C3poDataSource.getConnection();Statement stmt = conn.createStatement()){
            if (!conn.getAutoCommit())
                conn.setAutoCommit(true);
            stmt.execute("SET FOREIGN_KEY_CHECKS=0"); //disabilito il controllo delle FK in fase di inserimento

            try ( PreparedStatement st = conn.prepareStatement(sql)) {
                //operazione singola non necessita di fare rallback in caso di errore
                st.setInt(1, request.getRequestId());
                st.setInt(2, request.getActiveContract().getContractId());
                st.setInt(3, service.getServiceId());
                if (st.executeUpdate() != 1) //già esiste questa modifica
                    throw new IllegalStateException("Non è possibile inserire la modifica: " +
                            "controlla lo stato della richiesta\n");
            } catch (SQLException | IllegalStateException e) {
                e.printStackTrace();
                throw e;
            }finally {
                //comando critico che viene eseguito anche se viene rilanciata l'eccezione catturata
                stmt.execute("SET FOREIGN_KEY_CHECKS=1"); //riabilito il controllo delle FK
            }
        }catch (SQLException e){
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
            throws IllegalArgumentException, NullPointerException,SQLException {

        if (request == null ) throw new NullPointerException("Specificare una richiesta\n");
        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argomento della richiesta deve essere di tipo RemoveServiceModification\n");

        RemoveServiceModification modification = (RemoveServiceModification)request.getModification();
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
                    return false;
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
    public List<RequestBean> getRequests(ActiveContract activeContract, String sender){
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
                  continue;

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
    public List<RequestBean> getSubmits(ActiveContract activeContract, String receiver){
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
                       continue;

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
