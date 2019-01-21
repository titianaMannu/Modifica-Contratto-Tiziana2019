package entity;

public enum TypeOfPayment {
    CREDIT_CARD(0),
    VISA(1),
    PAYPAL(2),
    WIRE_TRANSFER(3);

    private int value;
    TypeOfPayment(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
