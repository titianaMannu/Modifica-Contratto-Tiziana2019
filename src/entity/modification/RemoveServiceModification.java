package entity.modification;
import entity.ActiveContract;
import entity.OptionalService;

import java.util.List;


public class RemoveServiceModification extends Modification {

    public RemoveServiceModification(Object objectToChange) throws IllegalArgumentException {
        setObjectToChange(objectToChange);
    }

    @Override
    protected void setObjectToChange(Object objectToChange) throws IllegalArgumentException {
        super.setObjectToChange(objectToChange);
        if (!(objectToChange instanceof OptionalService)) {
            throw new IllegalArgumentException("*******Argument must be an  OptionalService instance*******\n");
        }
    }

    @Override
    public OptionalService getObjectToChange() {
        return  (OptionalService)super.getObjectToChange();
    }

    @Override
    public boolean validate(ActiveContract activeContract) {
        OptionalService service = (OptionalService)objectToChange;
        List<OptionalService> newList = activeContract.getServiceList();
        for (OptionalService item : newList)
            if (item.getServicePrice() == service.getServicePrice() &&
                    item.getServiceName().equals(service.getServiceName()) )
                return true;
        return false;
    }

    @Override
    public void update(ActiveContract contract) {
        List<OptionalService> list = contract.getServiceList();
        list.remove(getObjectToChange()); //rimozione del servizio
        contract.setGrossPrice(); // ricalcolo del prezzo lordo
    }


}