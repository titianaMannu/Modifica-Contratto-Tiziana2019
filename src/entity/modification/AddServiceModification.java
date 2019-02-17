package entity.modification;
import entity.ActiveContract;
import entity.OptionalService;

import java.util.List;

public class AddServiceModification extends Modification{

    public AddServiceModification(Object objectToChange) throws  IllegalArgumentException{
        setObjectToChange(objectToChange);
    }

    @Override
    public boolean validate(ActiveContract activeContract) {
        OptionalService service = (OptionalService)this.objectToChange;
        List<OptionalService> newList = activeContract.getServiceList();
        for (OptionalService item : newList)
            if (item.getServicePrice() == service.getServicePrice() &&
                    item.getServiceName().equals(service.getServiceName()) )
                return false;
        return true;
    }

    @Override
    public void update(ActiveContract contract) {
        List<OptionalService> list = contract.getServiceList();
        list.add(getObjectToChange()); //aggiunta del servizio
        contract.setGrossPrice(); // ricalcolo del prezzo lordo
    }

    @Override
    protected void setObjectToChange(Object objectToChange) throws  IllegalArgumentException {
        super.setObjectToChange(objectToChange);
        if (!(objectToChange instanceof OptionalService)) {
            throw new IllegalArgumentException("*******Argument must be a OptionalService instance*******\n");
        }
    }

    @Override
    public OptionalService getObjectToChange() {
        return  (OptionalService)super.getObjectToChange();
    }


}
