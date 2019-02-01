package DAO.modificationDAO.paymentMethod;

import Beans.ActiveContract;
import DAO.ContractDao;
import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import entity.TypeOfPayment;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class PaymentMethodTest {

    private static RequestForModificationDao requestDao = ModificationDaoFActory.getInstance().createProduct(TypeOfModification.CHANGE_PAYMENTMETHOD);
    private static ActiveContract contract = ContractDao.getInstance().getContract(1);
    private static RequestForModification request = new RequestForModification(contract, TypeOfModification.CHANGE_PAYMENTMETHOD,
            TypeOfPayment.PAYPAL, "pippo", "", null, RequestStatus.PENDING);
    private RequestForModification request_bis = new RequestForModification(contract, TypeOfModification.CHANGE_PAYMENTMETHOD,
            TypeOfPayment.VISA, "pippo", "", null, RequestStatus.PENDING);


    @BeforeClass
    public static void setUp() throws Exception {
        requestDao.insertRequest(request);
    }

    @Test
    public void updateContract() throws SQLException {
        requestDao.updateContract(request);
        ActiveContract newcontract = ContractDao.getInstance().getContract(contract.getContractId());
        assertEquals(request.getModification().getObjectToChange(), newcontract.getPaymentMethod()); // (expected, current)
    }

    @Test
    public void validateRequest() throws SQLException {
        assertFalse(requestDao.validateRequest(request_bis));
    }
}