package entity.request;

import Beans.Contract;
import entity.modification.Modification;

public class AcceptedState extends State {

    public AcceptedState(Contract contract, Modification modfc) {
        super(contract, modfc);
        this.status = RequestStatus.ACCEPTED;
    }

    public State forward(RequestForModification requestForModification){
        return  new ClosedState(contract, modification);
    }

    public State decline(RequestForModification requestForModification){
        //nothing to do
        return requestForModification.getCurrentState();
    }

    private void applyModification(){
        //TODO tutto!

    }

}
