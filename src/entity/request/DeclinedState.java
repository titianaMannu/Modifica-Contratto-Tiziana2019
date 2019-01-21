package entity.request;

import Beans.Contract;
import entity.modification.Modification;

public class DeclinedState extends State {

    public DeclinedState(Contract contract, Modification mdfc) {
        super(contract, mdfc);
        this.status= RequestStatus.DECLINED;
    }

    @Override
    public State forward(RequestForModification requestForModification) {
        return new ClosedState(contract, modification);
    }

    @Override
    public State decline(RequestForModification requestForModification) {
        return requestForModification.getCurrentState();
    }
}
