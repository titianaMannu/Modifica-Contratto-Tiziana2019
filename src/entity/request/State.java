package entity.request;

import Beans.Contract;
import entity.modification.Modification;

public abstract class State {
    //per il DB
    protected RequestStatus status;
    //il contratto è parte dello stato della proposta
    protected Contract contract;
    //la modifica è parte dello stato della proposta
    protected Modification modification;

    public State(Contract contract, Modification modfc) throws IllegalArgumentException{
        setContract(contract);
        setModification(modfc);
    }

    protected void setModification(Modification modfc) throws IllegalArgumentException{
        if (modfc== null) throw new IllegalArgumentException();
        Contract tempC = modfc.update(contract);
        if (!tempC.equals(contract)){
            //confronto con equals:
            this.modification = modfc;
        }
        else //modifica non ha effetti sul contratto
            throw new IllegalArgumentException();
    }


    protected void setContract(Contract c)throws IllegalArgumentException{
        if ( c == null) throw new IllegalArgumentException();
        this.contract = c;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Contract getContract() {
        return contract;
    }

    public abstract State forward(RequestForModification requestForModification);
    public abstract State decline(RequestForModification requestForModification);
}
