package DAO.modificationDAO;

import Beans.Contract;
import DAO.C3poDataSource;
import entity.modification.Modification;
import entity.modification.TerminationDateModification;
import entity.request.RequestForModification;

import java.sql.*;
import java.time.LocalDate;

public class TerminationDateModfcDao implements ModificationDao {

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
    public boolean updateContract(Contract c) {
        return false;
    }

    @Override
    public boolean insertModification(Modification modification, RequestForModification request)
            throws IllegalArgumentException{
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
    public boolean deleteModification(Modification modification) {
        return false;
    }

    @Override
    public boolean validateModification(Modification modification, Contract contract) throws IllegalArgumentException{
        if (!(modification instanceof TerminationDateModification)) {
            throw new IllegalArgumentException();
        }
        //prima controllo se avrebbe degli efetti sul contratto
        if (!modification.validate(contract))
            return false;

        String sql = "select count(newDate) as numOfRequests\n" +
                "from TerminationDateModification as m join requestForModification as rm on m.requestId = rm.idRequest" +
                " && m.requestC = ?\nwhere rm.status = 0" ;

        try (Connection conn = C3poDataSource.getConnection(); PreparedStatement st = conn.prepareStatement(sql)){
            st.setInt(1, contract.getContractId());
            ResultSet res = st.executeQuery();

            if (res.next()) {
                //per lo stesso contratto ci deve essere 1 sola richiesta PENDING di questo tipo
                closeResources(conn, st);
                return res.getInt("numOfRequests") == 0;
            }
        }catch (SQLException e){
            //TODO Gestione errore
        }

        //se arrivo qui qualcosa è andato storto
        return false;
    }

    public void closeResources(Connection conn, Statement st )throws SQLException{
        conn.close();
        st.close();
    }

    @Override
    public Modification getModification() {
        return null;
    }
}
