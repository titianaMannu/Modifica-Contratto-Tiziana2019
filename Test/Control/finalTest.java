package Control;


import DAO.contractDaoTest.afterTest;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Suite.SuiteClasses(value = {beforeTest.class, InsertRequestModel.class, SubmitModelTest.class, DeleteRequestCtrl.class,  afterTest.class})
public class finalTest {
    //todo press play : esecuzione del test in ordine ascendente automatica
}
