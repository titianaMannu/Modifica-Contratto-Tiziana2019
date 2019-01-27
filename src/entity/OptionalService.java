package entity;

import java.util.Objects;

public class OptionalService {
    private int serviceId;
    private  String serviceName;
    private int servicePrice;
    private String description;

    public OptionalService(int serviceId, String serviceName, int servicePrice, String description) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
        this.description = description;
    }

    public OptionalService(int serviceId, String serviceName, int servicePrice) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
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
        return "OptionalService{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceDescription='" +
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
