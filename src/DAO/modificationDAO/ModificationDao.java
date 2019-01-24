package DAO.modificationDAO;

import Beans.Contract;
import entity.modification.Modification;
import entity.request.RequestForModification;

public interface ModificationDao {

    boolean updateContract(Contract c, Modification modification);//FATTO !
    boolean insertModification(Modification modification, RequestForModification request);//FATTO!
    boolean deleteModification(Modification modification);
    boolean validateModification(Modification modification, Contract contract); //FATTO!
    Modification getModification();

}
