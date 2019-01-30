package Control;

import Beans.ErrorMsg;
import Beans.RequestBean;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SubmitModelTest {
    private static SubmitModel control = new SubmitModel();
    private static List<RequestBean> list ;
    @BeforeClass
    public static void setUp() throws Exception {
        control.setActiveContract(1);
        control.setUserNickname("pluto");
        list = control.getSubmits();
    }

    @Test
    public void accept() {
        ErrorMsg err;
        for (RequestBean item : list ){
            err = control.accept(item);
            assertFalse(err.isErr());
        }
    }

}