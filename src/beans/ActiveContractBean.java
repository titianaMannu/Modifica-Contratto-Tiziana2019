package beans;

import enumeration.TypeOfPayment;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActiveContractBean implements Serializable {
    private int contractId;
    private LocalDate stipulationDate;
    private LocalDate terminationDate;
    private TypeOfPayment paymentMethod;
    private String tenantNickname;
    private String renterNickname;
    private int grossPrice; // netto + costi servizi
    private int netPrice;
    private int frequencyOfPayment; // Mesi
    private List<OptionalServiceBean> serviceList;

    public ActiveContractBean(int contractId, LocalDate stipulationDate, LocalDate terminationDate,
                              TypeOfPayment paymentMethod, String tenantNickname, String renterNickname,
                              int grossPrice, int netPrice, int frequencyOfPayment) {
        this.contractId = contractId;
        this.stipulationDate = stipulationDate;
        this.terminationDate = terminationDate;
        this.paymentMethod = paymentMethod;
        this.tenantNickname = tenantNickname;
        this.renterNickname = renterNickname;
        this.grossPrice = grossPrice;
        this.netPrice = netPrice;
        this.frequencyOfPayment = frequencyOfPayment;
        this.serviceList = new ArrayList<>();
    }

    public void addService(OptionalServiceBean serviceBean){
        serviceList.add(serviceBean);
    }

    public int getContractId() {
        return contractId;
    }

    public LocalDate getStipulationDate() {
        return stipulationDate;
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

    public int getGrossPrice() {
        return grossPrice;
    }

    public int getNetPrice() {
        return netPrice;
    }

    public int getFrequencyOfPayment() {
        return frequencyOfPayment;
    }

    public List<OptionalServiceBean> getServiceList() {
        return serviceList;
    }
}
