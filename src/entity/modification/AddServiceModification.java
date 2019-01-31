package entity.modification;
import Beans.ActiveContract;
import Beans.OptionalService;
import java.util.List;

public class AddServiceModification extends Modification{

    public AddServiceModification(Object objectToChange) throws IllegalArgumentException{
        setObjectToChange(objectToChange);
    }

    @Override
    public boolean validate(ActiveContract activeContract) {
        OptionalService service = (OptionalService)objectToChange;
        List<OptionalService> newList = activeContract.getServiceList();
        for (OptionalService item : newList)
            if (item.getServicePrice() == service.getServicePrice() &&
                    item.getServiceName().equals(service.getServiceName()) )
                return false;
        return true;
    }

    @Override
    public void setObjectToChange(Object objectToChange) throws IllegalArgumentException {
        super.setObjectToChange(objectToChange);
        if (!(objectToChange instanceof OptionalService)) {
            throw new IllegalArgumentException("*******Argument must be a Service instance*******\n");
        }
        super.setObjectToChange(objectToChange);
    }

    @Override
    public OptionalService getObjectToChange() {
        return  (OptionalService)super.getObjectToChange();
    }


}
