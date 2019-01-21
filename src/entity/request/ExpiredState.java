package entity.request;

import Beans.Contract;
import entity.modification.Modification;

public class ExpiredState extends State{

    public ExpiredState(Contract contract, Modification modfc) {
        super(contract, modfc);
        this.status = RequestStatus.EXPIRED;
    }

    public State forward(RequestForModification requestForModification){
        return new ClosedState(contract, modification);
    }

    public State decline(RequestForModification requestForModification){
        //nothing to do
        return requestForModification.getCurrentState();
    }
}
