package DAO.modificationDAO;
import enumeration.TypeOfModification;

public class ModificationDaoFActory {

    private static class LazyContainer{
        private static final ModificationDaoFActory instance = new ModificationDaoFActory();
    }

    public static ModificationDaoFActory getInstance(){
        return LazyContainer.instance;
    }

    private ModificationDaoFActory() {
        // default constructor must be private because of we are using singleton pattern
    }

    public RequestForModificationDao createProduct(TypeOfModification inType) throws IllegalArgumentException{
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
                throw new IllegalArgumentException("tipologia di modifica non prevista\n");
        }

    }

}
