package entity;

import beans.OptionalServiceBean;

public class OptionalService {
        private int serviceId = -1;
        private  String serviceName;
        private int servicePrice;
        private String description;

        public OptionalService(int serviceId, String serviceName, int servicePrice, String description)
                throws IllegalArgumentException{
            setServiceId(serviceId);
            this.servicePrice = servicePrice;
            this.serviceName = serviceName;
            this.description = description;
        }

        /**
         * costruttore per service a cui non è stato ancora assegnato un id
         */
        public OptionalService(String serviceName, int servicePrice, String description) {
            this.servicePrice = servicePrice;
            this.serviceName = serviceName;
            this.description = description;
        }


        public void setServiceId(int serviceId)throws IllegalArgumentException{
            if (serviceId < 1) throw  new IllegalArgumentException("serviceId non corretto\n");
            this.serviceId = serviceId;
        }


        public String getDescription() {
            return description;
        }

        public String getServiceName() {
            return serviceName;
        }

        public int getServiceId() throws IllegalStateException {
            if (this.serviceId < 0 ) throw new IllegalStateException();
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
            OptionalService optionalService = (OptionalService) object;
            return serviceId == optionalService.serviceId;
        }

        public OptionalServiceBean makeBean()throws IllegalArgumentException {
            if (this.serviceId < 1) throw  new IllegalArgumentException("serviceId non corretto\n");
            return  new OptionalServiceBean(serviceId, serviceName, servicePrice, description);
        }

}

