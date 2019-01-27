package entity.request;

/**
 * PENDING: il Mittente ha inviato la richiesta che ancora non è stata visualizzata. Non è possibile cambiare i parametri della richiesta!
 * ACCEPTED: La richiesta è stata accetata. Il Mittente non ha visualizzato la risposta
 * DECLINED: il Destinatario ha considerato e rifutato la proposta; il Mittente non ha ancora visionato la risposta
 * EXPIRED: richiesta scaduta. Il mittente non ha visualizzato la notifica
 * CLOSED: il Mitente ha visualizzato la risposta.
 */
public enum RequestStatus {
    PENDING(0, "pending"),
    ACCEPTED(1, "accepted"),
    DECLINED(2, "declined"),
    EXPIRED(3, "expired"),
    CLOSED(4, "closed");

    private String description;
    private int current;
    RequestStatus(int status, String description){
        this.current = status;
        this.description = description;
    }

    public static RequestStatus valueOf(int inVal){
        for (RequestStatus e : values())
            if (e.getCurrentStatus() == inVal)
                return e;
        return null;
    }

    public String getDescription() {
        return description;
    }

    public int getCurrentStatus(){
       return this.current;
    }
}
