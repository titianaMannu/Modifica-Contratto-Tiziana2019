package entity.request;

import Beans.Contract;

public class AcceptedState extends State {

    public AcceptedState(Contract contract) {
        super(contract);
        this.status = RequestStatus.ACCEPTED;
    }

    public State forward(RequestForModification requestForModification){
        return  new ClosedState(contract);
    }

    public State stop(RequestForModification requestForModification){
        //nothing to do
        return requestForModification.getCurrentState();
    }

    private void applyModification(){
        //TODO tutto!
    }

}
