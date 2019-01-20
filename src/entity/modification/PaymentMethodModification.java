package entity.modification;

import Beans.Contract;
import entity.TypeOfPayment;

public class PaymentMethodModification extends Modification {

    public PaymentMethodModification(String reasonWhy, Object objectToChange){
        super(reasonWhy, objectToChange);
        try {
            if (!(objectToChange instanceof TypeOfPayment)) {
                throw new IllegalArgumentException("*******Argument must be a TypeOfPayment instance*******\n");
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
        if (c.getPaymentMethod().equals(objectToChange)){
            //nothing to change
            return c;
        }
        return new Contract(c.getContractId(), c.isExipired(), c.getInitDate(), c.getTerminationDate(),
                (TypeOfPayment) objectToChange, c.getTenantName(), c.getRenterName(), c.getTenantCF(), c.getRenterCF(),
                c.getGrossPrice(), c.getNetPrice(), c.getFrequencyOfPayment(), c.isReported(), c.getServiceList());
    }
}
