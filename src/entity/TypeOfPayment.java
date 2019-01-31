package entity;

public enum TypeOfPayment {
    CREDIT_CARD(0, "carta di credito"),
    VISA(1, "Visa"),
    PAYPAL(2, "Paypal"),
    WIRE_TRANSFER(3, "bonifico bancario");

    private int value;
    private String description;
    TypeOfPayment(int value, String description){
        this.value = value;
        this.description = description;
    }

    public static TypeOfPayment valueOf(int inVal){
        for (TypeOfPayment type : values())
            if (type.getValue() == inVal )
                return type;
        return null;
    }


    public static TypeOfPayment getType(String inVal){
        for (TypeOfPayment type : values())
            if (type.getDescription().equals(inVal) )
                return type;
        return null;
    }

    public String getDescription() {
        return description;
    }

    public int getValue(){
        return this.value;
    }
}
