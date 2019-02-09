package entity.modification;
import entity.ActiveContract;

public abstract class Modification {
    protected Object objectToChange;

    /**
     * @param activeContract : ActiveContract
     * @return true if the Modification is compatible with the activeContract; else return false
     */
    public abstract boolean validate(ActiveContract activeContract);


    public void setObjectToChange(Object objectToChange) throws IllegalArgumentException{
        if (objectToChange == null) throw new IllegalStateException("objectTochange hat to be not null");
        this.objectToChange = objectToChange;
    }

    public Object getObjectToChange() {
        return objectToChange;
    }

    @Override
    public String toString(){
        return  objectToChange.toString();
    }


}
