package DAO.contractDaoTest;

import Beans.ActiveContract;
import DAO.ContractDao;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

import static org.junit.Assert.*;


public class ContractDaoTest {
    @Test
    public void getContract() {
        ContractDao contractDao = ContractDao.getInstance();
        ActiveContract activeContract = contractDao.getContract(1);
        assertNotNull(activeContract);
        assertEquals("pippo", activeContract.getTenantNickname());
        assertEquals("pluto", activeContract.getRenterNickname());
        assertEquals("wifi", activeContract.getServiceList().get(0).getServiceName());
        //etc
        activeContract = contractDao.getContract(2);
        assertNull(activeContract);
    }

}