package entity.request;

import Beans.Contract;
import entity.modification.Modification;

public class PendingState extends State {

    public  PendingState(Contract contract, Modification modfc){
        super(contract, modfc);
        this.status = RequestStatus.PENDING;
    }
    public State forward(RequestForModification requestForModification){
        return new EvaluationState(contract, modification);
    }

    public State decline(RequestForModification requestForModification){
        return new ExpiredState(contract, modification);
    }
}
