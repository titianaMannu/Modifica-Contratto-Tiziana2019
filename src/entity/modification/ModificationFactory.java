package entity.modification;

import beans.OptionalServiceBean;
import entity.OptionalService;
import enumeration.TypeOfModification;

public class ModificationFactory {

    public static ModificationFactory getInstance() {
        return LazyContainer.instance;
    }

    private ModificationFactory() {
        // default constructor must be private because of we are using singleton pattern
    }

    private static class LazyContainer{
        private final static ModificationFactory instance = new ModificationFactory();
    }


    public Modification createProduct(Object objectToChange, TypeOfModification inType){
        try {
            switch (inType) {
                case ADD_SERVICE:
                    return createAddServiceModfc(objectToChange);
                case REMOVE_SERVICE:
                    return createRemoveServiceModfc(objectToChange);
                case CHANGE_PAYMENTMETHOD:
                    return createPaymentModfc(objectToChange);
                case CHANGE_TERMINATIONDATE:
                    return createTerminationDateModfc(objectToChange);
                default:
                    return null;
            }

        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return  null;
        }

    }

    /**
     * Se ho in input un bean viene fatta la creazione della entity service e passata alla modifica.
     */
    private Modification createAddServiceModfc(Object objectToChange) throws IllegalArgumentException{
        if (objectToChange instanceof OptionalServiceBean) {
            OptionalServiceBean obj = (OptionalServiceBean) objectToChange;
            objectToChange = new OptionalService(obj.getServiceName(), obj.getServicePrice(), obj.getDescription());
        }
        return new AddServiceModification(objectToChange);
    }

    private Modification createRemoveServiceModfc(Object objectToChange)
            throws  IllegalArgumentException {
        if (objectToChange instanceof OptionalServiceBean) {
            OptionalServiceBean obj = (OptionalServiceBean) objectToChange;
            objectToChange = new OptionalService(obj.getServiceId(), obj.getServiceName(), obj.getServicePrice(), obj.getDescription());
        }
        return new RemoveServiceModification(objectToChange);
    }

    private Modification createPaymentModfc(Object objectToChange) throws  IllegalArgumentException{
        return new PaymentMethodModification(objectToChange);
    }

    private Modification createTerminationDateModfc(Object objectToChange) throws IllegalArgumentException{
        return new TerminationDateModification(objectToChange);
    }
}
