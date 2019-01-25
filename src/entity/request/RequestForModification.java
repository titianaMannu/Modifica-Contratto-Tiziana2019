package entity.request;

import Beans.Contract;
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
    private int requestId;
    private Contract contract;
    private TypeOfModification type;
    private Modification modification;

    /**
     * Il controllo sui parametri viene fatto nelle funzioni setter (Incapsulamento della logica i controllo ui dati)
     * Per rendere le entità il più autonome possibili
     * @param c : Contract
     * @param sender : String
     * @param date
     */
    public RequestForModification(Contract c, TypeOfModification type, Object obj, String sender, String reasonWhy,
                                  LocalDate date) throws  IllegalArgumentException, IllegalStateException{

        buildModification(obj, type);
        if (c == null)
            throw new IllegalArgumentException();
        setType(type);
        setReasonWhy(reasonWhy);
        setDateOfSubmission(date);
        setSenderReceiver(sender, c);
    }


    public void buildModification(Object obj, TypeOfModification type) throws IllegalArgumentException{
        Modification modification = ModificationFactory.getInstance().createProduct(obj, type);
        if (modification == null){
            throw new IllegalStateException();
        }
        this.modification = modification;
    }

    public void setSenderReceiver(String sender, Contract c) throws IllegalArgumentException{
        // controllo sui dati
        if (sender == null || c == null || sender.isEmpty())
            throw  new IllegalArgumentException();

        if (sender.equals(c.getTenantNickname()) ) { // sender corrisponde a tenant
            this.senderNickname = sender;
            this.receiverNickname = c.getRenterNickname();
        }
        else if (sender.equals(c.getRenterNickname())){ // sender corrisponde a renter
            this.senderNickname = sender;
            this.receiverNickname = c.getTenantNickname();
        }
        else throw  new IllegalArgumentException(); // se non ho corrispondenza lancio eccezione

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
            throw new IllegalArgumentException();
        this.type = type;
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

    public Contract getContract() {
        return contract;
    }

    public int getRequestId() {
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
