package entity.modification;
import entity.ActiveContract;

public abstract class Modification {
    protected Object objectToChange;

    /**
     * @param activeContract : ActiveContract
     * @return true se la modifica è compatibile con activeContract
     */
    public abstract boolean validate(ActiveContract activeContract);

    /**
     * applica l'oggetto della modifica al contratto
     * @param contract : ActiveContract
     */
    public abstract void update(ActiveContract contract);

    protected void setObjectToChange(Object objectToChange) throws IllegalArgumentException{
        if (objectToChange == null) throw new IllegalArgumentException("objectTochange hat to be not null");
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
