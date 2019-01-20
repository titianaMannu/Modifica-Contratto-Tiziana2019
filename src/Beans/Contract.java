package Beans;

import entity.OptionalService;
import entity.TypeOfPayment;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * di fatto è un bean che ha l'unico scopo di contenere le informazioni
 * È  importante che non ci siano setter! I parametri si definiscono tutti nel costruttore.
 */
public class Contract implements Serializable {
    private long contractId;
    private boolean isExipired;
    private LocalDate initDate;
    private LocalDate terminationDate;
    private TypeOfPayment paymentMethod;
    private String tenantName;
    private String renterName;
    private String tenantCF;
    private String renterCF;
    private int grossPrice; // Prezzo rata con inclusi costi servizi
    private int netPrice; // prezzo netto per l'affitto
    private int frequencyOfPayment; // Mesi
    private boolean reported; // Serve per il Termina contratto, se c'è già una segnalazione pendente
    private List<OptionalService> serviceList;


    public Contract(long contractId, boolean isExpired, LocalDate initDate, LocalDate terminationDate,
                    TypeOfPayment paymentMethod, String tenantName, String renterName, String tenantCF,
                    String renterCF, int grossPrice, int netPrice, int frequencyOfPayment, boolean reported,
                    List<OptionalService> serviceList) {

        this.contractId = contractId;
        this.isExipired = isExpired;
        this.initDate = initDate;
        this.terminationDate = terminationDate;
        this.paymentMethod = paymentMethod;
        this.tenantName = tenantName;
        this.renterName = renterName;
        this.tenantCF = tenantCF;
        this.renterCF = renterCF;
        this.grossPrice = grossPrice;
        this.netPrice = netPrice;
        this.frequencyOfPayment = frequencyOfPayment;
        this.reported = reported;
        this.serviceList = serviceList;
    }

    public long getContractId() {
        return contractId;
    }

    public boolean isExipired() {
        return isExipired;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public TypeOfPayment getPaymentMethod() {
        return paymentMethod;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getRenterName() {
        return renterName;
    }

    public String getTenantCF() {
        return tenantCF;
    }

    public String getRenterCF() {
        return renterCF;
    }

    public int getGrossPrice() {
        return grossPrice;
    }

    public int getNetPrice() {
        return netPrice;
    }

    public int getFrequencyOfPayment() {
        return frequencyOfPayment;
    }

    public boolean isReported() {
        return reported;
    }

    /**
     * non posso usare direttamente clone() perché farebbe una shallow copy e ritornerebbe l'indirizzo della lista
     * e chiunque potrebbe mofdificarla dall'esterno!!
     * @return List <OptionalService>
     */
    public List<OptionalService> getServiceList() {
        List<OptionalService> deepCopy = new ArrayList<>(serviceList);
      //  deepCopy.addAll(serviceList);
        return deepCopy;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "contractId=" + contractId +
                ", isExipired=" + isExipired +
                ", initDate=" + initDate +
                ", terminationDate=" + terminationDate +
                ", paymentMethod=" + paymentMethod +
                ", tenantName='" + tenantName + '\'' +
                ", renterName='" + renterName + '\'' +
                ", tenantCF='" + tenantCF + '\'' +
                ", renterCF='" + renterCF + '\'' +
                ", grossPrice=" + grossPrice +
                ", netPrice=" + netPrice +
                ", frequencyOfPayment=" + frequencyOfPayment +
                ", reported=" + reported +
                ", serviceList=" + serviceList +
                '}';
    }
}
