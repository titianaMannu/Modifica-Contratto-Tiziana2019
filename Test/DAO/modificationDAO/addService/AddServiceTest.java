package DAO.modificationDAO.addService;

import Beans.ActiveContract;
import Beans.RequestBean;
import DAO.ContractDao;
import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import Beans.OptionalService;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.SQLException;

import java.util.List;

import static org.junit.Assert.*;

public class AddServiceTest {

    private static RequestForModificationDao requestDao = ModificationDaoFActory.getInstance().createProduct(TypeOfModification.ADD_SERVICE);
    private static ActiveContract contract = ContractDao.getInstance().getContract(1);
    private static RequestForModification request1 = new RequestForModification(contract, TypeOfModification.ADD_SERVICE,
            new OptionalService("wifi", 30, ""), "pippo", "", null, RequestStatus.PENDING);
    private static RequestForModification request2 = new RequestForModification(contract, TypeOfModification.ADD_SERVICE,
            new OptionalService("pulizia", 30, ""), "pippo", "", null, RequestStatus.PENDING);

    @BeforeClass
    public static void setUp() throws Exception {
        requestDao.insertRequest(request1);
        requestDao.insertRequest(request2);
    }

    @Test
    public void updateContract()
            throws IllegalStateException, IllegalArgumentException, NullPointerException, SQLException {
        ActiveContract newcontract;
        List<RequestBean> requests = requestDao.getRequests(contract, "pippo");
        for (RequestBean r : requests) {
            RequestForModification request = new RequestForModification(contract, r.getType(),
                    r.getObjectToChange(), r.getSender(), r.getReasonWhy(),
                    r.getDate(), r.getStatus());
            requestDao.updateContract(request);
            newcontract = ContractDao.getInstance().getContract(1);
            OptionalService service = (OptionalService) request.getModification().getObjectToChange();
            assertEquals(contract.getGrossPrice() + service.getServicePrice(), newcontract.getGrossPrice());
            contract = newcontract;
        }
    }


    @Test
    public void validateRequest() throws SQLException {
        assertFalse(requestDao.validateRequest(request1));
        assertFalse(requestDao.validateRequest(request2));
    }


}