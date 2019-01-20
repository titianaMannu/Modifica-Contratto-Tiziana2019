package entity.modification;

import Beans.Contract;
import entity.OptionalService;

import java.util.List;

public class AddServiceModification extends Modification{

    public AddServiceModification(String reasonWhy, Object objectToChange){
        super(reasonWhy, objectToChange);
        try {
            if (!(objectToChange instanceof OptionalService)) {
                throw new IllegalArgumentException("*******Argument must be a Service instance*******\n");
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
    public Contract update(Contract c){
        List<OptionalService> newList = c.getServiceList();
        //System.out.println(newList);
        if (newList.contains(objectToChange)){
            //nothing to change
            return c;
        }
        OptionalService service = (OptionalService) objectToChange;
        //aggiunta del nuovo servizio alla lista di supporto
        newList.add(service);
        return new Contract(c.getContractId(), c.isExipired(), c.getInitDate(), c.getTerminationDate(),
                c.getPaymentMethod(), c.getTenantNickname(), c.getRenterNickname(), c.getTenantCF(), c.getRenterCF(),
                c.getGrossPrice() + service.getServicePrice() , c.getNetPrice(), c.getFrequencyOfPayment(), c.isReported(), newList);
    }
}
