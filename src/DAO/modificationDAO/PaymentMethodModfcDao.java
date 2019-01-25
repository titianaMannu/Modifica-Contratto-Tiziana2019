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

public class PaymentMethodModfcDao extends RequestForModificationDao {

    private static PaymentMethodModfcDao ourInstance = null;

    private PaymentMethodModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static PaymentMethodModfcDao getInstance(){
        if (ourInstance == null)
            ourInstance = new PaymentMethodModfcDao();
        return ourInstance;
    }

    @Override
    public boolean updateContract(RequestForModification request) throws  IllegalStateException,IllegalArgumentException {
        if (request.getStatus() != RequestStatus.ACCEPTED)
            throw new IllegalStateException("Request is not accepted yet\n");
        if (! (request.getModification() instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        String sql ="update ActiveContract set paymentMethod = ?\n" +
                "where idContract = ?";

        PaymentMethodModification modification = (PaymentMethodModification) request.getModification();
        TypeOfPayment type = modification.getObjectToChange();
        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, type.getValue());
            st.setInt(2, request.getContract().getContractId());
            if (st.executeUpdate() == 1)
                return true;

        }catch (SQLException e){
            //TODO something
        }
        return false;
    }

    @Override
    public boolean insertModification(RequestForModification request) throws IllegalArgumentException{
        Modification modification = request.getModification();
        if (! (modification instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        String sql = "insert into PaymentMethodModification(requestId, requestC, paymentMethod) values (?, ?, ?)" ;
        try(Connection conn = C3poDataSource.getConnection();
            PreparedStatement st = conn.prepareStatement(sql) ){

            TypeOfPayment type = (TypeOfPayment)modification.getObjectToChange();
            st.setInt(1, request.getRequestId());
            st.setInt(2, request.getContract().getContractId());
            st.setInt(3, type.getValue());
            if (st.executeUpdate() == 1)
                return true;

        }catch (SQLException e){
            //TODO Gestione errore
        }
        return false;
    }

    @Override
    public boolean deleteModification(RequestForModification request) {
        return false;
    }

    @Override
    public boolean validateModification(RequestForModification request) throws IllegalArgumentException{
        if (!(request.getModification() instanceof PaymentMethodModification)) throw new IllegalArgumentException();

        PaymentMethodModification modification = (PaymentMethodModification) request.getModification();
        //prima controllo se avrebbe degli efetti sul contratto
        if(!modification.validate(request.getContract()))
            return false;

        String sql = "select count(paymentMethod) as numOfRequests\n" +
                "from PaymentMethodModification as m join requestForModification as rm on m.requestId = rm.idRequest" +
                " && m.requestC = ?  where rm.status = 0";

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, request.getContract().getContractId());
            ResultSet res = st.executeQuery();
            if (res.next())
                return res.getInt("numOfRequests") == 0;
        }catch (SQLException e){
            //TODO Gestione errore
        }

        //se arrivo qui qualcosa è andato storto
        return false;
    }


    @Override
    public Modification getModification(int idRequest, int idContract) {
        String sql = "select paymentMethod\n" +
                "from PaymentMethodModification\n" +
                "where requestId = ? && requestC = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, idRequest);
            st.setInt(2, idContract);
            ResultSet res = st.executeQuery();
            if (res.next())
                return ModificationFactory.getInstance().createProduct(TypeOfPayment.valueOf(res.getInt(1)),
                        TypeOfModification.CHANGE_PAYMENTMETHOD);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<RequestForModification> getSubmits(Contract contract, String sender) {
        List<RequestForModification> list = new ArrayList<>();
        String sql = "select  dateOfSubmission, reasonWhy, idRequest\n" +
                "from requestForModification\n" +
                "where  (contract, senderNickname, type) in ((?, ?, ?))";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, contract.getContractId());
            st.setString(2, sender);
            st.setInt(3, TypeOfModification.CHANGE_PAYMENTMETHOD.getValue());
            ResultSet res = st.executeQuery();
            while(res.next()){
                //tipo di modifica è di tipo CHANGE_PAYMENTMETHOD in questo caso
                Modification modfc = getModification(res.getInt(3), contract.getContractId());
                RequestForModification request = new RequestForModification(contract, TypeOfModification.CHANGE_PAYMENTMETHOD,
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

}
