package entity.request;

import Beans.Contract;

public class ExpiredState extends State{

    public ExpiredState(Contract contract) {
        super(contract);
        this.status = RequestStatus.EXPIRED;
    }

    public State forward(RequestForModification requestForModification){
        return new ClosedState(contract);
    }

    public State stop(RequestForModification requestForModification){
        //nothing to do
        return requestForModification.getCurrentState();
    }
}
