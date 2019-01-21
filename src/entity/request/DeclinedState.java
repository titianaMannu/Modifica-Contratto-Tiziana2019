package entity.request;

import Beans.Contract;

public class DeclinedState extends State {

    public DeclinedState(Contract contract) {
        super(contract);
        this.status= RequestStatus.DECLINED;
    }

    @Override
    public State forward(RequestForModification requestForModification) {
        return new ClosedState(contract);
    }

    @Override
    public State stop(RequestForModification requestForModification) {
        return requestForModification.getCurrentState();
    }
}
