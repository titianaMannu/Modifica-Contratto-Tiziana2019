package entity.modification;
import Beans.ActiveContract;
import entity.OptionalService;

import java.util.List;


public class RemoveServiceModification extends Modification {

    public RemoveServiceModification(Object objectToChange) throws IllegalArgumentException {
        super(objectToChange);
        if (!(objectToChange instanceof OptionalService)) {
            throw new IllegalArgumentException("*******Argument must be a  Service instance*******\n");
        }

    }

    @Override
    public void setObjectToChange(Object objectToChange) throws IllegalArgumentException {
        if (!(objectToChange instanceof OptionalService)) {
            throw new IllegalArgumentException("*******Argument must be a  Service instance*******\n");
        }
        super.setObjectToChange(objectToChange);
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
}