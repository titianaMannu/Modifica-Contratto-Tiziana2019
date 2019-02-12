package control;

import beans.ErrorMsg;
import beans.RequestBean;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EvaluateControlTest {
    private static EvaluateControl control ;
    private static List<RequestBean> list ;
    @BeforeClass
    public static void setUp() throws Exception {
        control = new EvaluateControl("pippo", 1);
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