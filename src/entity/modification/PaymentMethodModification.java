package entity.modification;
import Beans.Contract;
import entity.TypeOfPayment;

public class PaymentMethodModification extends Modification {

    public PaymentMethodModification(Object objectToChange) throws IllegalArgumentException{
        super(objectToChange);
        if (!(objectToChange instanceof TypeOfPayment)) {
            throw new IllegalArgumentException("*******Argument must be a TypeOfPayment instance*******\n");
        }
    }

    @Override
    public boolean validate(Contract contract) {
        if (contract.getPaymentMethod().equals(this.getObjectToChange()))
            return false;
        return true;
    }

    @Override
    public void setObjectToChange(Object objectToChange) throws IllegalArgumentException {
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

