package DAO;

import Beans.Contract;
import entity.modification.Modification;

public interface ModificationDao {

    boolean updateContract(Contract c);
    boolean insertModification(Modification modification);
    boolean deleteModification(Modification modification);
    boolean validateModification(Modification modification, Contract contract);
    Modification getModification();

}
