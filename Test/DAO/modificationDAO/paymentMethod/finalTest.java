package DAO.modificationDAO.paymentMethod;

import DAO.modificationDAO.afterTest;
import DAO.modificationDAO.beforeTest;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Suite.SuiteClasses(value = {beforeTest.class, PaymentMethodTest.class, afterTest.class})
public class finalTest {

}

