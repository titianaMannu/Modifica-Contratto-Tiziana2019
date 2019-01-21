package entity.request;

import Beans.Contract;

public class EvaluationState extends State {

    public EvaluationState(Contract contract) {
        super(contract);
        this.status= RequestStatus.EVALUATING;
    }

    @Override
    public State forward(RequestForModification requestForModification) {
        return new AcceptedState(contract);
    }

    @Override
    public State stop(RequestForModification requestForModification) {
        return new DeclinedState(contract);
    }
}
