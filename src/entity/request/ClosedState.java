package entity.request;

import Beans.Contract;
import entity.modification.Modification;

public class ClosedState extends State {
    public ClosedState(Contract contract, Modification modfc) {
        super(contract, modfc);
        this.status = RequestStatus.CLOSED;
    }

    @Override
    public State forward(RequestForModification requestForModification) {
        return requestForModification.getCurrentState();
    }

    @Override
    public State decline(RequestForModification requestForModification) {
        return requestForModification.getCurrentState();
    }
}
