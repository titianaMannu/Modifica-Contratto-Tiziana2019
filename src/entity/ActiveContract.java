package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActiveContract implements Serializable {
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
                          List<OptionalService> serviceList, int frequencyOfPayment) throws IllegalArgumentException {

        setContractId(contractId);
        setServiceList(serviceList);
        setFrequencyOfPayment(frequencyOfPayment);
        setPaymentMethod(paymentMethod);
        setPeriod(stipulationDate, terminationDate);
        setPriceInfo(netPrice);
        setRenterNickname(renterName);
        setTenantNickname(tenantName);
    }

    public int  getContractId() {
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

    public List<OptionalService> getServiceList() {
        return serviceList;
    }

    private void setContractId(int contractId) throws IllegalArgumentException{
        if (contractId < 0 ) throw new IllegalArgumentException("id del contratto non corretto\n");
        this.contractId = contractId;
    }

    private void setStipulationDate(LocalDate stipulationDate)throws IllegalArgumentException {
        if (stipulationDate == null)
            throw  new IllegalArgumentException("data stipulazione non specificata\n");
        if ( !stipulationDate.isAfter(LocalDate.now())) //data stipulazione deve essere antecedente o uguale al giorno corrente
             this.stipulationDate = stipulationDate;
        else throw new IllegalArgumentException("Data stipulazione non corretta\n");
    }

    private void setTerminationDate(LocalDate terminationDate)throws IllegalArgumentException {
        if (terminationDate == null)
            throw new IllegalArgumentException("Data di terminazione non specificata\n");
        if (stipulationDate.isAfter(terminationDate) || stipulationDate.equals(terminationDate))
            throw new IllegalArgumentException("data di terminazione non compatibile con quella di stipulazione\n");
        this.terminationDate = terminationDate;
    }

    private void setTenantNickname(String tenantNickname)throws IllegalArgumentException {
        if (tenantNickname != null && !tenantNickname.isEmpty())
            this.tenantNickname = tenantNickname;
        else throw new IllegalArgumentException("tenant nickname non corretto\n");

        if (renterNickname != null && renterNickname.equals(tenantNickname) )
            throw new IllegalArgumentException("renter e tenant devono avere nickname differenti\n");

    }

    private void setRenterNickname(String renterNickname) throws IllegalArgumentException {
        if (renterNickname!= null && !renterNickname.isEmpty())
             this.renterNickname = renterNickname;
        else throw new IllegalArgumentException("renter nickname non corretto\n");

        if (tenantNickname != null && tenantNickname.equals(renterNickname) )
            throw new IllegalArgumentException("renter e tenant devono avere nickname differenti\n");
    }

    private void setGrossPrice() {
        grossPrice = netPrice;
        if (serviceList == null ) return;
        for (OptionalService item : serviceList)
            grossPrice += item.getServicePrice();
    }

    private void setNetPrice(int netPrice) throws IllegalArgumentException{
        if (netPrice < 1) throw new IllegalArgumentException("Prezzo non significativo\n");
        this.netPrice = netPrice;
    }

    private void setFrequencyOfPayment(int frequencyOfPayment) throws IllegalArgumentException{
        if (frequencyOfPayment < 1) throw new IllegalArgumentException("frequenza (mensile) di pagamento non corretta\n");
        this.frequencyOfPayment = frequencyOfPayment;
    }

    private void setServiceList(List<OptionalService> serviceList) {
        if (serviceList == null)
            this.serviceList = new ArrayList<>();
        else this.serviceList = serviceList;
    }

    public void setPeriod(LocalDate stipulationDate, LocalDate terminationDate)throws IllegalArgumentException{
        //prima si definisce la data di stipulazione e poi quella di terminazione
        setStipulationDate(stipulationDate);
        setTerminationDate(terminationDate);
    }

    public void setPaymentMethod(TypeOfPayment paymentMethod)throws IllegalArgumentException {
        if (paymentMethod == null) throw new IllegalArgumentException("tipo di pagamento non specificato\n");
        this.paymentMethod = paymentMethod;
    }

    /**
     *Operazione setPriceInfo che stabilisce sia  grossPrice che netPrice
     */
    public void setPriceInfo(int netPrice) throws IllegalArgumentException{
        setNetPrice(netPrice);
        setGrossPrice();
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
}
