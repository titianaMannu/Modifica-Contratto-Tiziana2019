package entity.modification;
import Beans.Contract;

import java.time.LocalDate;

public class TerminationDateModification extends Modification {


    public TerminationDateModification(Object objectToChange)throws  IllegalArgumentException{
        super(objectToChange);
        if (!(objectToChange instanceof LocalDate)) {
            throw new IllegalArgumentException("*******Argument must be a  LocalDate instance*******\n");
        }
    }

    @Override
    public boolean validate(Contract contract) {
        LocalDate newDate = this.getObjectToChange();
        if (newDate.isBefore(LocalDate.now().plusDays(30)) | contract.getTerminationDate().equals(newDate))
            return false;
        return true;
    }

    @Override
    public void setObjectToChange(Object objectToChange) throws IllegalArgumentException {
        if (!(objectToChange instanceof LocalDate)) {
            throw new IllegalArgumentException("*******Argument must be a  LocalDate instance*******\n");
        }
        super.setObjectToChange(objectToChange);
    }

    @Override
    public LocalDate getObjectToChange() {
        return (LocalDate)this.objectToChange;
    }
}
