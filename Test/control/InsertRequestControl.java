package control;

import beans.ErrorMsg;
import beans.RequestBean;
import beans.OptionalServiceBean;
import entity.modification.TypeOfModification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
* todo test veri non demo
 * l'intento non è quello di coprire tutti i casi possibili ma solo quelli con valenza applicativa
 */

public class InsertRequestControl {

    private static RequestControl requestControl;
    private RequestBean request;

    @BeforeClass
    public static void setUp() throws Exception {
      requestControl = new RequestControl("pippo", 1);
    }

    @Test
    public void insertRequest() {
        request= new RequestBean("pippo" , TypeOfModification.ADD_SERVICE,
                new OptionalServiceBean("pulizia", 30, ""), LocalDate.now());
        ErrorMsg err = requestControl.insertRequest(request);
        assertFalse(err.isErr());
    }

}