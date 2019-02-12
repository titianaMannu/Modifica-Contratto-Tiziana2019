package control;

import beans.ErrorMsg;
import beans.RequestBean;
import entity.request.RequestStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteRequestCtrl {

    private static RequestControl requestControl;
    private static List<RequestBean> list ;

    @BeforeClass
    public  static void setUp(){
        requestControl = new RequestControl("pippo", 1);
        list = requestControl.getAllRequests();
    }

    @Test
    public void deleteRequest(){
        ErrorMsg err;
        for (RequestBean item : list ){
            err = requestControl.deleteRequest(item);
            if (item.getStatus() == RequestStatus.PENDING )
                assertTrue(err.isErr());
            else {
                list = requestControl.getAllRequests();

                assertFalse(err.isErr());
                err = requestControl.deleteRequest(list.get(list.indexOf(item)));
                if (err.isErr()) System.out.println(err.getMsgList().get(0));
                assertFalse(err.isErr());
            }
        }

    }

}
