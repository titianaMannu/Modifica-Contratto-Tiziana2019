package entity.modification;
import Beans.Contract;
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
    public boolean validate(Contract contract) {
        List<OptionalService> newList = contract.getServiceList();
        if (!newList.contains(this.objectToChange))
            return false;
        return true;
    }
}