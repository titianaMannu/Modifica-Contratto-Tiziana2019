package entity.request;

import Beans.Contract;
import entity.modification.Modification;
import java.time.LocalDate;

/**
 *ContectStateVM
 */

public class RequestForModification {
    private State currentState;
    private String senderNickname;
    private String receiverNickname;
    private LocalDate dateOfSubmission;

    /**
     * Il controllo sui parametri viene fatto nelle funzioni setter (Incapsulamento della logica i controllo ui dati)
     * Per rendere le entità il più autonome possibili
     * @param c : Contract
     * @param modfc : Modification
     * @param sender : String
     */
    public RequestForModification(Contract c, Modification modfc, String sender) throws  IllegalArgumentException{
        this.currentState = new PendingState(c, modfc); // non appena creata la proposta questa deve essere pending
        this.dateOfSubmission = LocalDate.now(); //La data della proposta è quella corrente
        this.setSenderReceiver(sender, currentState.getContract());
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

    /**
     * Normale avanzamento di stato nell'happy scenario
     */
    public void forward(){
        setState(this.currentState.forward(this));
    }

    /**
     *Avanzamento di stato nella direzione di decline o  expired
     */
    public void  decline(){
        setState(this.currentState.decline(this));
    }

    /**
     * state method has to be private
     * if not,  the currentState could be corrupted
     * @param st : State
     */
    private void setState(State st){
        this.currentState = st;
    }

    public State getCurrentState() {
        return currentState;
    }

    @Override
    public String toString() {
        return "RequestForModification{" +
                "currentState=" + currentState +
                ", senderNickname='" + senderNickname + '\'' +
                ", receiverNickname='" + receiverNickname + '\'' +
                ", dateOfSubmission=" + dateOfSubmission +
                '}';
    }


}
