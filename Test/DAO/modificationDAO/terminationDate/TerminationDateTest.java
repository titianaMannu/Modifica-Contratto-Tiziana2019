package DAO.modificationDAO.terminationDate;

import Beans.ActiveContract;
import DAO.ContractDao;
import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import entity.TypeOfPayment;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class TerminationDateTest {

    private RequestForModificationDao requestDao = ModificationDaoFActory.getInstance().createProduct(TypeOfModification.CHANGE_TERMINATIONDATE);
    private ActiveContract contract = ContractDao.getInstance().getContract(1);
    private RequestForModification request = new RequestForModification(contract, TypeOfModification.CHANGE_TERMINATIONDATE,
            LocalDate.of(2019, 12, 31), "pippo", "", null, RequestStatus.PENDING);
    private RequestForModification request_bis = new RequestForModification(contract, TypeOfModification.CHANGE_TERMINATIONDATE,
            LocalDate.of(2019, 12, 31), "pippo", "", null, RequestStatus.PENDING);

    @Before()
    public void setUp() throws Exception {
        requestDao.insertRequest(request);
    }

    @Test
    public void updateContract() throws SQLException {
        requestDao.updateContract(request);
        ActiveContract newcontract = ContractDao.getInstance().getContract(contract.getContractId());
        assertEquals(request.getModification().getObjectToChange(), newcontract.getTerminationDate()); // (expected, current)
    }

    @Test
    public void validateRequest() throws SQLException {
        assertFalse(requestDao.validateRequest(request_bis));
    }
}