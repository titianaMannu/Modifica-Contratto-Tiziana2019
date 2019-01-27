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
public class ActiveContract implements Serializable {
    private int contractId; // importante!
    private LocalDate initDate; // importante!
    private LocalDate terminationDate; // importante!
    private TypeOfPayment paymentMethod; // importante!
    private String tenantNickname; // importante!
    private String renterNickname;// importante!
    private String tenantCF;
    private String renterCF;
    private int grossPrice; // Prezzo rata + costi servizi; importante!
    private int netPrice; // prezzo netto per l'affitto
    private int frequencyOfPayment; // Mesi
    private List<OptionalService> serviceList; // importante!


    public ActiveContract(int contractId, LocalDate initDate, LocalDate terminationDate,
                          TypeOfPayment paymentMethod, String tenantName, String renterName, int grossPrice, int netPrice,
                          List<OptionalService> serviceList) {

        this.contractId = contractId;
        this.initDate = initDate;
        this.terminationDate = terminationDate;
        this.paymentMethod = paymentMethod;
        this.tenantNickname = tenantName;
        this.renterNickname = renterName;
        this.grossPrice = grossPrice;
        this.netPrice = netPrice;
        this.serviceList = serviceList;
    }



    public int  getContractId() {
        return contractId;
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

    public String getTenantNickname() {
        return tenantNickname;
    }

    public String getRenterNickname() {
        return renterNickname;
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ActiveContract activeContract = (ActiveContract) object;
        return contractId == activeContract.contractId;
    }


}
