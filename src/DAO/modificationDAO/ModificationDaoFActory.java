package DAO.modificationDAO;
import entity.modification.*;

public class ModificationDaoFActory {

    private static ModificationDaoFActory ourInstance = null;

    public static synchronized  ModificationDaoFActory getInstance() {
        if (ourInstance == null)
            ourInstance = new ModificationDaoFActory();
        return ourInstance;
    }

    private ModificationDaoFActory() {
        // default constructor must be private because of we are using singleton pattern
    }

    public RequestForModificationDao createProduct(TypeOfModification inType){
        switch (inType){
            case ADD_SERVICE:
                return AddServiceModfcDao.getInstance();
            case REMOVE_SERVICE:
                return RemoveServiceModfcDao.getInstance();
            case CHANGE_PAYMENTMETHOD:
                return PaymentMethodModfcDao.getInstance();
            case CHANGE_TERMINATIONDATE:
                return TerminationDateModfcDao.getInstance();
            default:
                return null;
        }

    }

}
