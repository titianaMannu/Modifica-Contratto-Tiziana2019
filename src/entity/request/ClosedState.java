package entity.request;

import Beans.Contract;

public class ClosedState extends State {
    public ClosedState(Contract contract) {
        super(contract);
        this.status = RequestStatus.CLOSED;
    }

    @Override
    public State forward(RequestForModification requestForModification) {
        return requestForModification.getCurrentState();
    }

    @Override
    public State stop(RequestForModification requestForModification) {
        return requestForModification.getCurrentState();
    }
}
