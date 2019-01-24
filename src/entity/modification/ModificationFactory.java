package entity.modification;

public class ModificationFactory {

    private static ModificationFactory ourInstance = null;

    /**
     * using singleton pattern to obtain the only one factory
     */
    public static synchronized ModificationFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new ModificationFactory();
        return ourInstance;
    }

    private ModificationFactory() {
        // default constructor must be private because of we are using singleton pattern
    }

    /**
     * @param objectToChange Object
     * @param inType TypeOfModification
     * @return Modification
     */
    public Modification createProduct(Object objectToChange, TypeOfModification inType){
        try {
            switch (inType) {
                case ADD_SERVICE:
                    return new AddServiceModification(objectToChange);
                case REMOVE_SERVICE:
                    return new RemoveServiceModification(objectToChange);
                case CHANGE_PAYMENTMETHOD:
                    return new PaymentMethodModification(objectToChange);
                case CHANGE_TERMINATIONDATE:
                    return new TerminationDateModification(objectToChange);
                default:
                    return null;
            }

        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return  null;
        }

    }
}
