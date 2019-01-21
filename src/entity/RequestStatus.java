package entity;

/**
 * PENDING: il Mittente ha inviato la richiesta che ancora non è stata visualizzata. È possibile modificare i parametri della richiesta
 * EVALUATING: Il Destinatario sta visualizzando e valutando la risposta. Non è possibile modificare i parametri della richiesta.
 * ACCEPTED: La richiesta è stata accetata. Il Mittente non ha visualizzato la risposta
 * DECLINED: il Destinatario ha considerato e rifutato la proposta; il Mittente non ha ancora visionato la risposta
 * EXPIRED: richiesta scaduta. Il mittente non ha visualizzato la notifica
 * CLOSED: il Mitente ha visualizzato la risposta.
 */
public enum RequestStatus {
    PENDING(0),
    EVALUATING(1),
    ACCEPTED(2),
    DECLINED(3),
    EXPIRED(4),
    CLOSED(5);

    private int current;
    RequestStatus(int status){
        this.current = status;
    }

    public int getCurrentStatus(){
       return this.current;
    }
}
