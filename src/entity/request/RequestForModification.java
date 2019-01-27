package entity.request;

import Beans.ActiveContract;
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
    private int requestId =  -1;
    private ActiveContract activeContract;
    private TypeOfModification type;
    private Modification modification;

    /**
     * Il controllo sui parametri viene fatto nelle funzioni setter (Incapsulamento della logica i controllo ui dati)
     * Per rendere le entità il più autonome possibili
     * @param c : ActiveContract
     * @param sender : String
     * @param date
     */
    public RequestForModification(int requestId, ActiveContract c, TypeOfModification type, Object obj, String sender, String reasonWhy,
                                  LocalDate date, RequestStatus status) throws  IllegalArgumentException{

        setRequestId(requestId);
        setModification(obj, type);
        if (c == null)
            throw new IllegalArgumentException("Contratto inserito non corretto\n");
        this.activeContract = c;
        setType(type);
        setReasonWhy(reasonWhy);
        setDateOfSubmission(date);
        setSenderReceiver(sender, c);
        setStatus(status);
    }

    public void setRequestId(int requestId) {
        //si assume che l'id della richesta sia un # non negativo
        if (requestId >= 0)
            this.requestId = requestId;
        else this.requestId = -1;
    }

    public void setModification(Object obj, TypeOfModification type) throws IllegalArgumentException{
        Modification modification = ModificationFactory.getInstance().createProduct(obj, type);
        if (modification == null){
            throw new IllegalArgumentException("Parametri della modifica non corretti\n");
        }
        this.modification = modification;
    }

    public void setSenderReceiver(String sender, ActiveContract c) throws IllegalArgumentException{
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


    /**
     * se reasonwhy è null o non specificato allora viene inserito un messaggio di default
     * @param reasonWhy
     */
    public void setReasonWhy(String reasonWhy){
        if (reasonWhy != null && !reasonWhy.isEmpty())
            this.reasonWhy = reasonWhy;
        else
            this.reasonWhy = "L'utente non ha specificato una motivazione.";
    }


    public void setType(TypeOfModification type) throws IllegalArgumentException{
        if( type == null)
            throw new IllegalArgumentException("tipo della modifica non corretto\n");
        this.type = type;
    }

    public void setStatus(RequestStatus status) throws  IllegalArgumentException{
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
    public String toString() {
        return "RequestForModification{" +
                ", senderNickname='" + senderNickname + '\'' +
                ", receiverNickname='" + receiverNickname + '\'' +
                ", dateOfSubmission=" + dateOfSubmission +
                '}';
    }


}
