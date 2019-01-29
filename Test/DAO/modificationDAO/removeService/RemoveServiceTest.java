package DAO.modificationDAO.removeService;

import Beans.ActiveContract;
import Beans.RequestBean;
import DAO.ContractDao;
import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import entity.OptionalService;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class RemoveServiceTest {

    private static RequestForModificationDao requestDao = ModificationDaoFActory.getInstance().createProduct(TypeOfModification.REMOVE_SERVICE);
    private static ActiveContract contract = ContractDao.getInstance().getContract(1);
    private static RequestForModification request1 = new RequestForModification(contract, TypeOfModification.REMOVE_SERVICE,
            contract.getServiceList().get(0), "pippo", "", null, RequestStatus.PENDING);

    @BeforeClass
    public static void setUp() throws Exception {
        requestDao.insertRequest(request1);
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
            assertEquals(contract.getGrossPrice() - service.getServicePrice(), newcontract.getGrossPrice());
            contract = newcontract;
        }
    }

    @Test
    public void validateRequest() throws SQLException {
        assertFalse(requestDao.validateRequest(request1));
    }
}