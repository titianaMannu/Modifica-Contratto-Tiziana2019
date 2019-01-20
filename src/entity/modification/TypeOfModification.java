package entity.modification;

public enum TypeOfModification {
    // insieme delle modifiche ammissibili

    ADD_SERVICE("Aggiungi un servizio"),
    REMOVE_SERVICE("Rimuovi un servizio"),
    CHANGE_TERMINATIONDATE("Cambia la data di scadenza"),
    CHANGE_PAYMENTMETHOD("Cambia metodo di pagamento");

    private String description;

    private TypeOfModification(String description){
        this.description = description;
    }

    public  String getDescription(){
        return this.description;
    }

}
