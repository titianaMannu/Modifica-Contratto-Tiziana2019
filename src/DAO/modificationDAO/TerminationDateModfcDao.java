package DAO.modificationDAO;

import DAO.C3poDataSource;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.TerminationDateModification;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;

import java.sql.*;
import java.time.LocalDate;

public class TerminationDateModfcDao extends RequestForModificationDao {

    private static TerminationDateModfcDao ourInstance = null;

    private TerminationDateModfcDao() {
        // default constructor must be private because of we are using singleton pattern
    }

    public static TerminationDateModfcDao getInstance(){
        if (ourInstance == null)
            ourInstance = new TerminationDateModfcDao();
        return ourInstance;
    }

    @Override
    public boolean updateContract(RequestForModification request) throws IllegalArgumentException{
        if (! (request.getModification() instanceof TerminationDateModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        TerminationDateModification modification = (TerminationDateModification) request.getModification();
        LocalDate date = modification.getObjectToChange();
        String sql ="update ActiveContract set terminationDate = ?\n" +
                "where idContract = ?";

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setDate(1, Date.valueOf(date));
            st.setInt(2, request.getContract().getContractId());
            if (st.executeUpdate() == 1)
                return true;

        }catch (SQLException e){
            //TODO something
        }

        return false;
    }

    @Override
    public boolean insertModification(RequestForModification request)
            throws IllegalArgumentException{
        Modification modification = request.getModification();
        if (! (modification instanceof TerminationDateModification))
            throw new IllegalArgumentException("Argument had to be AddServiceModificationType");

        String sql = "insert into TerminationDateModification(requestId, requestC, newDate) values (?, ?, ?)" ;
        try(Connection conn = C3poDataSource.getConnection();
            PreparedStatement st = conn.prepareStatement(sql) ){

            LocalDate date = (LocalDate)modification.getObjectToChange();
            st.setInt(1, request.getRequestId());
            st.setInt(2, request.getContract().getContractId());
            st.setDate(3, Date.valueOf(date));
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
        if (!(request.getModification() instanceof TerminationDateModification)) {
            throw new IllegalArgumentException();
        }

        TerminationDateModification modification = (TerminationDateModification) request.getModification();
        //prima controllo se avrebbe degli efetti sul contratto
        if (!modification.validate(request.getContract()))
            return false;

        String sql = "select count(newDate) as numOfRequests\n" +
                "from TerminationDateModification as m join requestForModification as rm on m.requestId = rm.idRequest" +
                " && m.requestC = ?\nwhere rm.status = 0" ;

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, request.getContract().getContractId());
            ResultSet res = st.executeQuery();

            if (res.next())
                //per lo stesso contratto ci deve essere 1 sola richiesta PENDING di questo tipo
                return res.getInt("numOfRequests") == 0;
        }catch (SQLException e){
            //TODO Gestione errore
        }

        //se arrivo qui qualcosa è andato storto
        return false;
    }

    @Override
    public Modification getModification(int idRequest, int idContract) {
        String sql = "select newDate\n" +
                "from TerminationDateModification\n" +
                "where requestId = ? && requestC = ?";
        try(Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, idRequest);
            st.setInt(2, idContract);
            ResultSet res = st.executeQuery();
            if (res.next())
                return ModificationFactory.getInstance().createProduct(res.getDate(1).toLocalDate(),
                        TypeOfModification.CHANGE_TERMINATIONDATE);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
