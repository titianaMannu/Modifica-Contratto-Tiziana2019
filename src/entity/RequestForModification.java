package entity;

import Beans.Contract;
import entity.modification.Modification;

import java.time.LocalDate;

public class RequestForModification {
    private RequestStatus status;
    private String senderNickname;
    private String receiverNickname;
    private LocalDate dateOfSubmission;
    //aggragazione
    private Contract contract;
    //aggregazione
    private Modification modification;

    /**
     * Il controllo sui parametri viene fatto nelle funzioni setter (Incapsulamento della logica i controllo ui dati)
     * Per rendere le entità il più autonome possibili
     * @param c : Contract
     * @param modfc : Modification
     * @param sender : String
     * @throws IllegalArgumentException
     */
    public RequestForModification(Contract c, Modification modfc, String sender) throws  IllegalArgumentException{

        this.status= RequestStatus.PENDING; // non appena creata la proposta questa deve essere pending
        this.dateOfSubmission = LocalDate.now(); //La data della proposta è quella corrente
        this.setContract(c);
        this.setSenderReceiver(sender, contract);
        this.setModification(modfc);
    }

    public void setModification(Modification modfc) throws IllegalArgumentException{
        if (modfc== null) throw new IllegalArgumentException();
        Contract tempC = modfc.update(contract);
        if (!tempC.equals(contract)){
            //confronto con equals: si vuole confrontare esattamente gli indirizzi di memoria
            this.modification = modfc;
        }
        else //modifica non ha effetti sul contratto
            throw new IllegalArgumentException();
    }


    public void setSenderReceiver(String sender, Contract c) throws IllegalArgumentException{
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

    public void setContract(Contract c)throws IllegalArgumentException{
        if ( c == null) throw new IllegalArgumentException();
        this.contract = c;
    }
}
