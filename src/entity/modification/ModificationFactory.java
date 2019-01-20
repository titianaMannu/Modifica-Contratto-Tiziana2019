package entity.modification;

public class ModificationFactory {

    private static ModificationFactory ourInstance = null;

    /**
     * using singleton pattern to obtain the only one factory
     */
    public static synchronized ModificationFactory getInstance() {
        if (ourInstance == null)
            return new ModificationFactory();
        else
            return ourInstance;
    }

    private ModificationFactory() {
        // default constructor must be private because of we are using singleton pattern
    }

    /**
     *
     * @param reasonWhy String
     * @param objectToChange Object
     * @param inType TypeOfModification
     * @return Modification
     */
    public Modification createProduct(String reasonWhy, Object objectToChange, TypeOfModification inType){
        switch (inType){
            case ADD_SERVICE:
                return new AddServiceModification(reasonWhy, objectToChange);
            case REMOVE_SERVICE:
                return  new RemoveServiceModification(reasonWhy, objectToChange);
            case CHANGE_PAYMENTMETHOD:
                return new PaymentMethodModification(reasonWhy, objectToChange);
            case CHANGE_TERMINATIONDATE:
                return  new TerminationDateModification(reasonWhy, objectToChange);
            default:
                return null;
        }
    }
}
