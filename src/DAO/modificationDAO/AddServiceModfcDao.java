package DAO.modificationDAO;

import DAO.C3poDataSource;
import entity.OptionalService;
import entity.modification.AddServiceModification;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;

import java.sql.*;


public class AddServiceModfcDao extends RequestForModificationDao {

    private static AddServiceModfcDao ourInstance = null;

    private AddServiceModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static AddServiceModfcDao getInstance(){
        if (ourInstance == null )
            ourInstance = new AddServiceModfcDao();
        return ourInstance;
    }

    @Override
    public boolean updateContract(RequestForModification request)throws IllegalArgumentException {
        if (! (request.getModification() instanceof AddServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        AddServiceModification modification = (AddServiceModification)request.getModification();
        OptionalService service = modification.getObjectToChange();
        String sql_1, sql_2;
        sql_1= "update OptionalService set ActiveContract_idContract = ?\n" +
                "where idService = ?";
        sql_2 = "update ActiveContract set grossPrice = grossPrice + ?\n" +
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
    public boolean insertModification(RequestForModification request)throws IllegalArgumentException {
        Modification modification = request.getModification();
        if (! (modification instanceof AddServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        OptionalService service = (OptionalService) modification.getObjectToChange();
        String sql_1 = "insert into AddServiceModification(requestId, requestC, service) values (?, ?, ?)" ;
        String sql_2 = "insert into OptionalService(name, price) values (?, ?)";
        try( Connection conn = C3poDataSource.getConnection();PreparedStatement st_1 = conn.prepareStatement(sql_1);
                PreparedStatement st_2 = conn.prepareStatement(sql_2)){
            //per l'inserimento del servizio
            st_2.setString(1, service.getServiceName());
            st_2.setInt(2, service.getServicePrice());
            //per l'inserimento della modifica
            st_1.setInt(1, request.getRequestId());
            st_1.setInt(2, request.getContract().getContractId());
            st_1.setInt(3, service.getServiceId());
            //dopo l'inserimento deve risultare il cambiamento di 1 riga per tabella
            if (st_2.executeUpdate() == 1 && st_1.executeUpdate()== 1)
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
    public boolean validateModification(RequestForModification request)throws IllegalArgumentException {
        if (! (request.getModification() instanceof AddServiceModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        AddServiceModification modification = (AddServiceModification)request.getModification();
        //prima controllo se avrebbe degli efetti sul contratto
        if (!modification.validate(request.getContract()))
            return false;

        OptionalService service =  modification.getObjectToChange();
        String sql = "select name as serviceName, price as servicePrice\n" +
                "from AddServiceModification as m join requestForModification as rm on m.requestId = rm.idRequest " +
                "&& m.requestC = ?\njoin OptionalService OS on m.service = OS.idService\n" +
                "where rm.status = 0" ;

        try (Connection  conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, request.getContract().getContractId());
            ResultSet res = st.executeQuery();

            while(res.next())
                if (service.getServiceName().equals(res.getString("serviceName"))
                        && service.getServicePrice() == res.getInt("servicePrice")){
                    return false;
                }

        }catch (SQLException e){
            //TODO Gestione errore
        }

        return true;
    }

    @Override
    public Modification getModification(int idRequest, int idContract) {
        String sql = "select name, price, description\n" +
                "from OptionalService as OS  join AddServiceModification ASM on OS.idService = ASM.service\n" +
                "where ASM.requestC = ? && ASM.requestId = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, idContract);
            st.setInt(1, idRequest);
            ResultSet res = st.executeQuery();
            if(res.next()){
                OptionalService service =new OptionalService(res.getString("name"), res.getString("description"),
                        res.getInt("price"));
                return ModificationFactory.getInstance().createProduct(service, TypeOfModification.ADD_SERVICE);
            }

        }catch(SQLException e){
           //TODO gestione errore
        }
        return null;
    }


}
