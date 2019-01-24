package DAO;

import Beans.Contract;
import entity.modification.Modification;

public class AddServiceModfcDao implements ModificationDao {
    @Override
    public boolean updateContract(Contract c) {
        return false;
    }

    @Override
    public boolean insertModification(Modification modification) {
        return false;
    }

    @Override
    public boolean deleteModification(Modification modification) {
        return false;
    }

    @Override
    public boolean validateModification(Modification modification, Contract contract) {
        return false;
    }

    @Override
    public Modification getModification() {
        return null;
    }
}
