package entity;

import org.jetbrains.annotations.NotNull;

public enum TypeOfPayment {
    CREDIT_CARD(0),
    VISA(1),
    PAYPAL(2),
    WIRE_TRANSFER(3);

    private int value;
    TypeOfPayment(int value){
        this.value = value;
    }

    public static TypeOfPayment valueOf(int inVal){
        for (TypeOfPayment type : values())
            if (type.getValue() == inVal )
                return type;
        return null;
    }

    public int getValue(){
        return this.value;
    }
}
