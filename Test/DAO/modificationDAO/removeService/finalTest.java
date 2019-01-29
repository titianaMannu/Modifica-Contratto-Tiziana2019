package DAO.modificationDAO.removeService;

import DAO.modificationDAO.afterTest;
import DAO.modificationDAO.terminationDate.TerminationDateTest;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Suite.SuiteClasses(value = {beforeTest.class, RemoveServiceTest.class, afterTest.class})
public class finalTest {

}

