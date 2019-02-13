package entity.modification;
import entity.ActiveContract;

import java.time.LocalDate;

public class TerminationDateModification extends Modification {


    public TerminationDateModification(Object objectToChange)throws  IllegalArgumentException{
        setObjectToChange(objectToChange);
    }

    @Override
    public boolean validate(ActiveContract activeContract) {
        LocalDate newDate = this.getObjectToChange();
        if (newDate.isBefore(LocalDate.now().plusDays(30)) | activeContract.getTerminationDate().equals(newDate))
            return false;
        return true;
    }

    @Override
    public void update(ActiveContract contract) {
        contract.setPeriod(contract.getStipulationDate(), getObjectToChange());
    }

    @Override
    protected void setObjectToChange(Object objectToChange) throws IllegalArgumentException{
        super.setObjectToChange(objectToChange);
        if (!(objectToChange instanceof LocalDate)) {
            throw new IllegalArgumentException("*******Argument must be a  LocalDate instance*******\n");
        }
    }

    @Override
    public LocalDate getObjectToChange() {
        return (LocalDate)this.objectToChange;
    }
}
