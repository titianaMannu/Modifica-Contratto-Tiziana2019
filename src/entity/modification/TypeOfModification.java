package entity.modification;

public enum TypeOfModification {
    // insieme delle modifiche ammissibili

    ADD_SERVICE(0, "Aggiungi un servizio"),
    REMOVE_SERVICE(1, "Rimuovi un servizio"),
    CHANGE_TERMINATIONDATE(2, "data di scadenza"),
    CHANGE_PAYMENTMETHOD(3, "metodo di pagamento");

    private String description;
    private int value;
    TypeOfModification(int value, String description){
        this.description = description;
        this.value  = value;
    }


    public  String getDescription(){
        return this.description;
    }

    /**
     * necessità di assegnare un valore numerico per identificarlo nel DB
     * @return
     */
    public int getValue(){
        return this.value;
    }

    public static TypeOfModification valueOf(int inVal){
        for (TypeOfModification item : values()){
            if (item.getValue() == inVal){
                return item;
            }
        }
        return null;
    }

}
