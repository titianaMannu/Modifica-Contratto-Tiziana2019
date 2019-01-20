package entity.modification;

import Beans.Contract;

import java.time.LocalDate;

public class TerminationDateModification extends Modification {


    public TerminationDateModification(String reasonWhy, Object objectToChange){
        super(reasonWhy, objectToChange);
        try {
            if (!(objectToChange instanceof LocalDate)) {
                throw new IllegalArgumentException("*******Argument must be a  LocalDate instance*******\n");
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
        LocalDate newDate = (LocalDate)objectToChange;
        if (newDate.isBefore(LocalDate.now()) | c.getTerminationDate().equals(objectToChange)){
            //messaggio di errore
            return c;
        }
        return new Contract(c.getContractId(), c.isExipired(), c.getInitDate(), (LocalDate)this.objectToChange,
                c.getPaymentMethod(), c.getTenantName(), c.getRenterName(), c.getTenantCF(), c.getRenterCF(),
                c.getGrossPrice(), c.getNetPrice(), c.getFrequencyOfPayment(), c.isReported(), c.getServiceList());
    }
}
