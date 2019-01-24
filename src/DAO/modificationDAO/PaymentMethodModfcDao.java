package DAO.modificationDAO;

import Beans.Contract;
import DAO.C3poDataSource;
import entity.TypeOfPayment;
import entity.modification.Modification;
import entity.modification.PaymentMethodModification;
import entity.request.RequestForModification;

import java.sql.*;

public class PaymentMethodModfcDao implements ModificationDao {

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
    public boolean updateContract(Contract c, Modification modification) throws  IllegalArgumentException {
        if (! (modification instanceof PaymentMethodModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        String sql ="update ActiveContract set paymentMethod = ?\n" +
                "where idContract = ?";

        TypeOfPayment type = (TypeOfPayment)modification.getObjectToChange();
        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, type.getValue());
            st.setInt(2, c.getContractId());
            if (st.executeUpdate() == 1)
                return true;

        }catch (SQLException e){
            //TODO something
        }
        return false;
    }

    @Override
    public boolean insertModification(Modification modification, RequestForModification request) throws IllegalArgumentException{
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
    public boolean deleteModification(Modification modification) {
        return false;
    }

    @Override
    public boolean validateModification(Modification modification, Contract contract) throws IllegalArgumentException{
        if (!(modification instanceof PaymentMethodModification)) throw new IllegalArgumentException();
        //prima controllo se avrebbe degli efetti sul contratto
        if(!modification.validate(contract))
            return false;

        String sql = "select count(paymentMethod) as numOfRequests\n" +
                "from PaymentMethodModification as m join requestForModification as rm on m.requestId = rm.idRequest" +
                " && m.requestC = 2  where rm.status = 0";

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, contract.getContractId());
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
    public Modification getModification() {
        return null;
    }
}
