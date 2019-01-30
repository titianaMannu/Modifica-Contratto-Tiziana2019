package Control;

import Beans.ErrorMsg;
import Beans.RequestBean;
import Beans.OptionalService;
import entity.modification.TypeOfModification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
* todo test veri non demo
 * l'intento non è quello di coprire tutti i casi possibili ma solo quelli con valenza applicativa
 */

public class InsertRequestModel {

    private static RequestModel requestModel = new RequestModel();
    private RequestBean request;

    @BeforeClass
    public static void setUp() throws Exception {
        requestModel.setActiveContract(1);
        requestModel.setUserNickname("pippo");
    }

    @Test
    public void insertRequest() {
        request= new RequestBean("pippo" , TypeOfModification.ADD_SERVICE,
                new OptionalService("pulizia", 30, ""), "", LocalDate.now());
        ErrorMsg err = requestModel.insertRequest(request);
        assertFalse(err.isErr());
    }

}