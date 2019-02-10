package entity.modification;

import beans.OptionalServiceBean;
import entity.OptionalService;

public class ModificationFactory {

    private static ModificationFactory ourInstance = new ModificationFactory();

    /**
     * using singleton pattern to obtain the only one factory
     */
    public static synchronized ModificationFactory getInstance() {
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
     * viene fatta la creazione della entity service e passata alla modifica.
     */
    private Modification createAddServiceModfc(Object objectToChange) throws IllegalArgumentException{
        if (objectToChange instanceof OptionalService)
            return new AddServiceModification(objectToChange);
        if (!(objectToChange instanceof OptionalServiceBean)) throw  new IllegalArgumentException();
        OptionalServiceBean obj = (OptionalServiceBean)objectToChange;
        OptionalService optionalService = new OptionalService(obj.getServiceName(), obj.getServicePrice(), obj.getDescription());
        return new AddServiceModification(optionalService);
    }

    private Modification createRemoveServiceModfc(Object objectToChange)
                        throws  IllegalArgumentException, IllegalStateException{
        if (objectToChange instanceof OptionalService)
            return new RemoveServiceModification(objectToChange);
        if (!(objectToChange instanceof OptionalServiceBean)) throw  new IllegalArgumentException();
        OptionalServiceBean obj = (OptionalServiceBean)objectToChange;
        OptionalService optionalService = new OptionalService(obj.getServiceId(), obj.getServiceName(), obj.getServicePrice(),
                obj.getDescription());
        return new RemoveServiceModification(optionalService);
    }

    private Modification createPaymentModfc(Object objectToChange) throws  IllegalArgumentException{
        return new PaymentMethodModification(objectToChange);
    }

    private Modification createTerminationDateModfc(Object objectToChange) throws IllegalArgumentException{
        return new TerminationDateModification(objectToChange);
    }
}
