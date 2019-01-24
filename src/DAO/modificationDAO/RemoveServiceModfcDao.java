package DAO.modificationDAO;

import Beans.Contract;
import DAO.C3poDataSource;
import entity.OptionalService;
import entity.modification.Modification;
import entity.modification.RemoveServiceModification;
import entity.request.RequestForModification;

import java.sql.*;

public class RemoveServiceModfcDao implements ModificationDao {

    private static RemoveServiceModfcDao ourInstance = null;


    private RemoveServiceModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static RemoveServiceModfcDao getInstance(){
        if (ourInstance == null)
            ourInstance = new RemoveServiceModfcDao();
        return ourInstance;
    }

    @Override
    public boolean updateContract(Contract c, Modification modification) {
        if (! (modification instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");


        OptionalService service = (OptionalService) modification.getObjectToChange();
        String sql_1, sql_2;
        sql_1= "update OptionalService set ActiveContract_idContract = ?\n" +
                "where idService = ?";
        sql_2 = "update ActiveContract set grossPrice = grossPrice - ?\n" +
                "where idContract = ?";
        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st1= conn.prepareStatement(sql_1);
             PreparedStatement st2 = conn.prepareStatement(sql_2)){

            st1.setInt(1, c.getContractId());
            st1.setInt(2, service.getServiceId());
            st2.setInt(1, service.getServicePrice());
            st2.setInt(2, c.getContractId());
            if (st1.executeUpdate() == 1 && st2.executeUpdate() == 1)
                return true;

        }catch (SQLException e){
            //TODO something
        }

        return false;
    }

    @Override
    public boolean insertModification( RequestForModification request) {
        Modification modification = request.getModification();
        if (! (modification instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        OptionalService service = (OptionalService) modification.getObjectToChange();
        String sql = "insert into RemoveServiceModification(requestId, requestC, service) values (?, ?, ?)" ;

        try(Connection conn = C3poDataSource.getConnection();
            PreparedStatement st = conn.prepareStatement(sql) ){

            st.setInt(1, request.getRequestId());
            st.setInt(2, request.getContract().getContractId());
            st.setInt(3, service.getServiceId());
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
    public boolean validateModification(Modification modification, Contract contract) {
        if (! (modification instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        //prima controllo se avrebbe degli efetti sul contratto
        if (!modification.validate(contract))
            return false;

        OptionalService service = (OptionalService) modification.getObjectToChange();

        String sql = "select name as serviceName, price as servicePrice\n" +
                "from RemoveServiceModification as m join requestForModification as rm on m.requestId = rm.idRequest " +
                "&& m.requestC = ?\n" +
                "join OptionalService OS on m.service = OS.idService\n" +
                "where rm.status = 0 ;" ;

        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){

            st.setInt(1, contract.getContractId());
            ResultSet res = st.executeQuery();

            while(res.next())
                if (service.getServiceName().equals(res.getString("serviceName"))
                        && service.getServicePrice() == res.getInt("servicePrice"))
                    return false;


        }catch (SQLException e){
            //TODO Gestione errore
        }

        return true;
    }

    @Override
    public Modification getModification() {
        return null;
    }


}
