package entity;

import java.util.Objects;

public class OptionalService {
    private  String serviceName;
    private String serviceDescription;
    private int servicePrice;

    public OptionalService(String serviceName, String serviceDescription, int servicePrice) {
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePrice = servicePrice;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public int getServicePrice() {
        return servicePrice;
    }

    @Override
    public String toString() {
        return "OptionalService{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceDescription='" + serviceDescription + '\'' +
                ", servicePrice=" + servicePrice +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OptionalService service = (OptionalService) object;
        return Objects.equals(serviceName, service.serviceName);
    }

}
