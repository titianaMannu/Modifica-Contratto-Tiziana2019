package Beans;

public class OptionalService {
    private int serviceId = -1;
    private  String serviceName;
    private int servicePrice;
    private String description;

    public OptionalService(int serviceId, String serviceName, int servicePrice, String description) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.description = description;
    }

    public OptionalService(String serviceName, int servicePrice, String description) {
        this.description = description;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
    }

    public OptionalService() {
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServicePrice(int servicePrice) {
        this.servicePrice = servicePrice;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
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
        return "[ " + serviceName + ", " +  servicePrice + " ]";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OptionalService service = (OptionalService) object;
        return serviceId == service.serviceId;
    }


}
