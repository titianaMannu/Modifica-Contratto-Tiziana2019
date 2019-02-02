package Beans;

import java.io.Serializable;


/**
 *OptionalService si occupa di incapsulare i propri dati e la loro logica di controllo
 */
public class OptionalService implements Serializable {
    private int serviceId = -1;
    private  String serviceName;
    private int servicePrice;
    private String description;

    public OptionalService(int serviceId, String serviceName, int servicePrice, String description)
            throws IllegalArgumentException {
        ErrorMsg msg = new ErrorMsg();
        msg.addAllMsg(setServiceName(serviceName));
        msg.addAllMsg(setServicePrice(servicePrice));
        msg.addAllMsg(setServiceId(serviceId));
        setDescription(description);

        if (msg.isErr()){
            String err = "";
            for (String str : msg.getMsgList()) {
                err += str;
            }
            throw new IllegalArgumentException(err);
        }

    }

    /**
     * costruttore per service a cui non è stato ancora assegnato un id
     */
    public OptionalService(String serviceName, int servicePrice, String description) throws IllegalArgumentException {
        ErrorMsg msg = new ErrorMsg();
        msg.addAllMsg(setServiceName(serviceName));
        msg.addAllMsg(setServicePrice(servicePrice));
        setDescription(description);

        if (msg.isErr()){
            String err = "";
            for (String str : msg.getMsgList()) {
                err += str;
            }
            throw new IllegalArgumentException(err);
        }
    }

    public OptionalService() {
        //Bean deve avere un costruttore di default
    }

    public ErrorMsg setServiceName(String serviceName){
        ErrorMsg msg = new ErrorMsg();
        if (serviceName!= null && !serviceName.isEmpty())
            this.serviceName = serviceName;
        else msg.addMsg("specificare nome del servizio\n");

        return msg;
    }

    public ErrorMsg setServicePrice(int servicePrice){
        ErrorMsg msg = new ErrorMsg();
        if (servicePrice < 1) msg.addMsg("specificare prezzo del servizio\n");
        this.servicePrice = servicePrice;

        return msg;
    }

    public void setDescription(String description) {
        if (description != null && !description.isEmpty())
            this.description = description;
        else
            this.description = "";
    }

    public ErrorMsg setServiceId(int serviceId) {
        ErrorMsg msg = new ErrorMsg();
        if (serviceId < 1) msg.addMsg("serviceId non corretto\n");
        this.serviceId = serviceId;

        return msg;
    }


    public String getDescription() {
        return description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public int getServicePrice() {
        return servicePrice;
    }

    @Override
    public String toString() {
        return  serviceName + "  prezzo: " +  servicePrice ;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OptionalService service = (OptionalService) object;
        return serviceId == service.serviceId;
    }


}
