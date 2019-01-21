package entity.request;

import Beans.Contract;

public class PendingState extends State {

    public  PendingState(Contract contract){
        super(contract);
        this.status = RequestStatus.PENDING;
    }
    public State forward(RequestForModification requestForModification){
        return new EvaluationState(contract);
    }

    public State stop(RequestForModification requestForModification){
        return new ExpiredState(contract);
    }
}
