package entity.request;

/**
 * PENDING: il Mittente ha inviato la richiesta che ancora non è stata visualizzata. Non è possibile cambiare i parametri della richiesta!
 * ACCEPTED: La richiesta è stata accetata. Il Mittente non ha visualizzato la risposta
 * DECLINED: il Destinatario ha considerato e rifutato la proposta; il Mittente non ha ancora visionato la risposta
 * EXPIRED: richiesta scaduta. Il mittente non ha visualizzato la notifica
 * CLOSED: il Mitente ha visualizzato la risposta.
 */
public enum RequestStatus {
    PENDING(0),
    ACCEPTED(1),
    DECLINED(2),
    EXPIRED(3),
    CLOSED(4);

    private int current;
    RequestStatus(int status){
        this.current = status;
    }

    public int getCurrentStatus(){
       return this.current;
    }
}
