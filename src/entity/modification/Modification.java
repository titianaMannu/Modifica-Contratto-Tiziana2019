package entity.modification;
import Beans.Contract;

public abstract class Modification {
    protected  String reasonWhy;
    protected Object objectToChange;

    public Modification(String reasonWhy, Object objectToChange){
        this.reasonWhy = reasonWhy;
        this.objectToChange = objectToChange;
    }

    public abstract Contract update(Contract contract);

    public String getReasonWhy() {
        return reasonWhy;
    }

    public Object getObjectToChange() {
        return objectToChange;
    }

    @Override
    public String toString(){
        return  objectToChange.toString() + "  sovrascrive l'originale perché: " + reasonWhy;
    }


}
