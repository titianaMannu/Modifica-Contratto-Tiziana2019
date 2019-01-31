package entity.modification;
import Beans.ActiveContract;
import entity.TypeOfPayment;

public class PaymentMethodModification extends Modification {

    public PaymentMethodModification(Object objectToChange) throws IllegalArgumentException{
       setObjectToChange(objectToChange);
    }

    @Override
    public boolean validate(ActiveContract activeContract) {
        if (activeContract.getPaymentMethod().equals(this.getObjectToChange()))
            return false;
        return true;
    }

    @Override
    public void setObjectToChange(Object objectToChange) throws IllegalArgumentException {
        super.setObjectToChange(objectToChange);
        if (!(objectToChange instanceof TypeOfPayment)) {
            throw new IllegalArgumentException("*******Argument must be a TypeOfPayment instance*******\n");
        }
        super.setObjectToChange(objectToChange);
    }

    @Override
    public TypeOfPayment getObjectToChange() {
        return (TypeOfPayment)this.objectToChange;
    }
}

