package entity.request;

/**
 * PENDING: il Mittente ha inviato la richiesta che ancora non è stata visualizzata. Non è possibile cambiare i parametri della richiesta.
 * ACCEPTED: La richiesta è stata accetata. Il Mittente non ha visualizzato la risposta
 * DECLINED: Il Destinatario ha considerato e rifutato la proposta; il Mittente non ha ancora visionato la risposta
 * TO_EXPIRE: si sta controllando se la richiesta è scaduta; questa viene posta in uno stato intermedio (può tornare PENDING o diventare EXPIRED)
 * EXPIRED: richiesta scaduta. Il mittente non ha visualizzato la notifica
 */
public enum RequestStatus {
    PENDING(0, "pending"),
    ACCEPTED(1, "accepted"),
    DECLINED(2, "declined"),
    TO_EXPIRE(3, ""),
    EXPIRED(4, "expired");

    private String description;
    private int current;
    RequestStatus(int status, String description){
        this.current = status;
        this.description = description;
    }

    public static RequestStatus valueOf(int inVal){
        for (RequestStatus e : values())
            if (e.getValue() == inVal)
                return e;
        return null;
    }

    public String getDescription() {
        return description;
    }

    public int getValue(){
       return this.current;
    }


}
