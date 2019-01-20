package entity.modification;

import Beans.Contract;
import entity.OptionalService;

import java.util.List;

public class RemoveServiceModification extends Modification{

    public RemoveServiceModification(String description, Object objectToChange){
        super(description, objectToChange);
        try {
            if (!(objectToChange instanceof OptionalService)) {
                throw new IllegalArgumentException("*******Argument must be a  Service instance*******\n");
            }

        }catch (IllegalArgumentException e){
            e.printStackTrace();
            //TODO gestione dell'errore in modo opportuno
            System.exit(1);
        }
    }

    /**
     * Non potendo modificare direttamente l'entità contratto se ne deve instanziare una nuova con i parametri modificati
     * TODO controllo degli errori
     * @param c
     * @return
     */
    @Override
    public Contract update(Contract c) {
        List<OptionalService> newList = c.getServiceList();
        if (!newList.contains(this.objectToChange)) {
            //nothing to change
            return c;
        }
        OptionalService service = newList.remove(newList.indexOf(objectToChange));
        return new Contract(c.getContractId(), c.isExipired(), c.getInitDate(), c.getTerminationDate(),
                c.getPaymentMethod(), c.getTenantName(), c.getRenterName(), c.getTenantCF(), c.getRenterCF(),
                c.getGrossPrice() - service.getServicePrice(), c.getNetPrice(), c.getFrequencyOfPayment(),
                c.isReported(), newList);

    }
}
