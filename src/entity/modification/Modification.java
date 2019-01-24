package entity.modification;
import Beans.Contract;

public abstract class Modification {
    protected Object objectToChange;
    protected  int idModification;

    public Modification(Object objectToChange) throws  IllegalArgumentException{
        this.idModification = -1;
        this.setObjectToChange(objectToChange);
    }

    /**
     * @param contract : Contract
     * @return true if the Modification is compatible with the contract; else return false
     */
    public abstract boolean validate(Contract contract);


    public void setObjectToChange(Object objectToChange) throws IllegalArgumentException{
        if (objectToChange == null) throw new IllegalStateException("objectTochange hat to be not null");
        this.objectToChange = objectToChange;
    }

    public int getIdModification() {
        return idModification;
    }

    public Object getObjectToChange() {
        return objectToChange;
    }

    @Override
    public String toString(){
        return  objectToChange.toString();
    }


}
