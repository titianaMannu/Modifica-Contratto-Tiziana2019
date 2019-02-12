package entity.request;

import entity.ActiveContract;
import entity.modification.Modification;
import entity.modification.ModificationFactory;
import entity.modification.TypeOfModification;

import java.time.LocalDate;


public class RequestForModification {
    private String senderNickname;
    private String receiverNickname;
    private String reasonWhy;
    private LocalDate dateOfSubmission;
    private RequestStatus status;
    private int requestId ;
    /** Aggrega il contratto: non ha senso che esista una rischiesta di modifica senza il contratto*/
    private ActiveContract activeContract;
    private TypeOfModification type;
    private Modification modification;

    /**
     * Il controllo sui parametri viene fatto nelle funzioni setter (Incapsulamento della logica i controllo sui dati)
     * Per rendere le entità il più autonome possibili
     */
    public RequestForModification(int requestId, ActiveContract c, TypeOfModification type, Object obj, String sender, String reasonWhy,
                                  LocalDate date, RequestStatus status) throws  IllegalArgumentException{

        setRequestId(requestId);
        setModification(obj, type);
        if (c == null)
            throw new IllegalArgumentException("Contratto inserito non corretto\n");
        this.activeContract = c;
        setReasonWhy(reasonWhy);
        setDateOfSubmission(date);
        setSenderReceiver(sender, c);
        setStatus(status);
    }

    public RequestForModification(ActiveContract c, TypeOfModification type, Object obj, String sender, String reasonWhy,
                                  LocalDate date, RequestStatus status) throws  IllegalArgumentException{

        this.requestId = -1;
        setModification(obj, type);
        if (c == null)
            throw new IllegalArgumentException("Contratto inserito non corretto\n");
        this.activeContract = c;
        setReasonWhy(reasonWhy);
        setDateOfSubmission(date);
        setSenderReceiver(sender, c);
        setStatus(status);
    }


    /**
     * valida la richiesta per il contratto
     */
    public boolean validate(){
        return modification.validate(activeContract);
    }

    /**
     * accetta la richiesta e applica la modifica al contratto
     */
    public void accept(){
        setStatus(RequestStatus.ACCEPTED);
        this.modification.update(this.activeContract);
    }

    /**
     * declina la richiesta di modifica
     */
    public void decline(){
        setStatus(RequestStatus.DECLINED);
    }

    /**
     * rende la richiesta obsoleta se b è true altrimenti torna PENDING
     * @param b : boolean
     */
    public void expire(boolean b){
        if (b)
            setStatus(RequestStatus.EXPIRED);
        else
            setStatus(RequestStatus.PENDING);
    }

    public void setRequestId(int requestId)throws IllegalArgumentException {
        if (requestId < 1) throw new IllegalArgumentException("Specificare una richiesta esistente\n");
        this.requestId = requestId;
    }

    private void setModification(Object obj, TypeOfModification type) throws IllegalArgumentException{
        Modification modification = ModificationFactory.getInstance().createProduct(obj, type);
        if (modification == null){
            throw new IllegalArgumentException("Parametri della modifica non corretti\n");
        }
        this.type = type;
        this.modification = modification;
    }

    private void setSenderReceiver(String sender, ActiveContract c) throws IllegalArgumentException{
        // controllo sui dati
        if (sender == null || c == null || sender.isEmpty())
            throw  new IllegalArgumentException("Mittente non corretto\n");

        if (sender.equals(c.getTenantNickname()) ) { // sender corrisponde a tenant
            this.senderNickname = sender;
            this.receiverNickname = c.getRenterNickname();
        }
        else if (sender.equals(c.getRenterNickname())){ // sender corrisponde a renter
            this.senderNickname = sender;
            this.receiverNickname = c.getTenantNickname();
        }
        // se non ho corrispondenza lancio eccezione
        else throw  new IllegalArgumentException("Mittente non coincide con attori coinvolti nel contratto\n");

    }


    private void setDateOfSubmission(LocalDate dateOfSubmission) {
        if (dateOfSubmission != null)
            this.dateOfSubmission = dateOfSubmission;
        else
            this.dateOfSubmission = LocalDate.now();
    }


    private void setReasonWhy(String reasonWhy){
        if (reasonWhy != null && !reasonWhy.isEmpty())
            this.reasonWhy = reasonWhy;
        else
            this.reasonWhy = "L'utente non ha specificato una motivazione.";
    }


    private void setStatus(RequestStatus status) throws  IllegalArgumentException{
        if (status == null) throw new  IllegalArgumentException("Stato della modifica non corretto\n");
        this.status = status;
    }


    public LocalDate getDateOfSubmission() {
        return dateOfSubmission;
    }

    public String getReasonWhy() {
        return reasonWhy;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public String getReceiverNickname() {
        return receiverNickname;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public TypeOfModification getType() {
        return type;
    }

    public Modification getModification() {
        return modification;
    }

    public ActiveContract getActiveContract() {
        return activeContract;
    }

    public int getRequestId(){
        return requestId;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RequestForModification request = (RequestForModification) object;
        return requestId == request.requestId;
    }


}
