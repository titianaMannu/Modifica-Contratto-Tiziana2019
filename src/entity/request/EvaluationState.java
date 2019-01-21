package entity.request;

import Beans.Contract;
import entity.modification.Modification;

public class EvaluationState extends State {

    public EvaluationState(Contract contract, Modification modfc) {
        super(contract, modfc);
        this.status= RequestStatus.EVALUATING;
    }

    @Override
    public State forward(RequestForModification requestForModification) {
        return new AcceptedState(contract, modification);
    }

    @Override
    public State decline(RequestForModification requestForModification) {
        return new DeclinedState(contract, modification);
    }
}
