package entity.request;

import Beans.Contract;

public abstract class State {
    //per il DB
    protected RequestStatus status;
    //lo stato del contratto contribuisce a quello della proposta
    protected Contract contract;

    public State(Contract contract) {
        this.contract = contract;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Contract getContract() {
        return contract;
    }

    public abstract State forward(RequestForModification requestForModification);
    public abstract State stop(RequestForModification requestForModification);
}
