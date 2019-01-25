package DAO.modificationDAO;

import DAO.C3poDataSource;
import entity.OptionalService;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.RemoveServiceModification;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;

import java.sql.*;

public class RemoveServiceModfcDao extends RequestForModificationDao {

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
    public boolean updateContract(RequestForModification request) {
        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        RemoveServiceModification modification = (RemoveServiceModification)request.getModification();
        OptionalService service = modification.getObjectToChange();
        String sql_1, sql_2;
        sql_1= "update OptionalService set ActiveContract_idContract = ?\n" +
                "where idService = ?";
        sql_2 = "update ActiveContract set grossPrice = grossPrice - ?\n" +
                "where idContract = ?";
        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st1= conn.prepareStatement(sql_1);
             PreparedStatement st2 = conn.prepareStatement(sql_2)){

            st1.setInt(1, request.getContract().getContractId());
            st1.setInt(2, service.getServiceId());
            st2.setInt(1, service.getServicePrice());
            st2.setInt(2, request.getContract().getContractId());
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
    public boolean deleteModification(RequestForModification request) {
        return false;
    }

    @Override
    public boolean validateModification(RequestForModification request) {
        if (! (request.getModification() instanceof RemoveServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");


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

            st.setInt(1, request.getContract().getContractId());
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
    public Modification getModification(int idRequest, int idContract) {
        String sql = "select name, price, description\n" +
                "from OptionalService as OS  join RemoveServiceModification ASM on OS.idService = ASM.service\n" +
                "where ASM.requestC = ? && ASM.requestId = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, idContract);
            st.setInt(1, idRequest);
            ResultSet res = st.executeQuery();
            if(res.next()){
                OptionalService service =new OptionalService(res.getString("name"), res.getString("description"),
                        res.getInt("price"));
                return ModificationFactory.getInstance().createProduct(service, TypeOfModification.REMOVE_SERVICE);
            }

        }catch(SQLException e){
            //TODO gestione errore
        }
        return null;
    }


}
