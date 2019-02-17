package entity;

import beans.ActiveContractBean;
import enumeration.TypeOfPayment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActiveContract  {
    private int contractId;
    private LocalDate stipulationDate;
    private LocalDate terminationDate;
    private TypeOfPayment paymentMethod;
    private String tenantNickname;
    private String renterNickname;
    private int grossPrice; // netto + costi servizi
    private int netPrice;
    private int frequencyOfPayment; // Mesi
    private List<OptionalService> serviceList;


    public ActiveContract(int contractId, LocalDate stipulationDate, LocalDate terminationDate,
                          TypeOfPayment paymentMethod, String tenantName, String renterName,  int netPrice,
                          List<OptionalService> serviceList, int frequencyOfPayment) {

        this.contractId = contractId;
        this.serviceList = new ArrayList<>(serviceList);
        this.frequencyOfPayment = frequencyOfPayment;
        this.paymentMethod = paymentMethod;
        this.stipulationDate = stipulationDate;
        this.terminationDate = terminationDate;
        this.renterNickname = renterName;
        this.tenantNickname = tenantName;
        this.netPrice = netPrice;
        setGrossPrice();
    }


    public String getTenantNickname() {
        return tenantNickname;
    }

    public String getRenterNickname() {
        return renterNickname;
    }

    public int  getContractId() {
        return contractId;
    }

    public TypeOfPayment getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }


    public List<OptionalService> getServiceList() {
        return serviceList;
    }


    public void setTerminationDate(LocalDate terminationDate)throws IllegalArgumentException {
        if (terminationDate == null)
            throw new IllegalArgumentException("Data di terminazione non specificata\n");
        if (this.stipulationDate.isAfter(terminationDate) || this.stipulationDate.equals(terminationDate))
            throw new IllegalArgumentException("data di terminazione non compatibile con quella di stipulazione\n");
        this.terminationDate = terminationDate;
    }


    public void setGrossPrice() {
        this.grossPrice = this.netPrice;
        for (OptionalService item : this.serviceList)
            this.grossPrice += item.getServicePrice();
    }



    public void setPaymentMethod(TypeOfPayment paymentMethod)throws IllegalArgumentException {
        if (paymentMethod == null) throw new IllegalArgumentException("tipo di pagamento non specificato\n");
        this.paymentMethod = paymentMethod;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ActiveContract activeContract = (ActiveContract) object;
        return contractId == activeContract.contractId;
    }

    @Override
    public String toString() {
        return "ActiveContract{" +
                "contractId=" + contractId +
                ", stipulationDate=" + stipulationDate +
                ", terminationDate=" + terminationDate +
                ", paymentMethod=" + paymentMethod +
                ", tenantNickname='" + tenantNickname + '\'' +
                ", renterNickname='" + renterNickname + '\'' +
                ", grossPrice=" + grossPrice +
                ", netPrice=" + netPrice +
                ", frequencyOfPayment=" + frequencyOfPayment +
                ", serviceList=" + serviceList +
                '}';
    }

    public ActiveContractBean makeBean(){
        ActiveContractBean contractBean = new ActiveContractBean(contractId, stipulationDate, terminationDate, paymentMethod,
                tenantNickname, renterNickname, grossPrice, netPrice, frequencyOfPayment);
        for (OptionalService item : serviceList){
            contractBean.addService(item.makeBean());
        }
        return contractBean;
    }

}
