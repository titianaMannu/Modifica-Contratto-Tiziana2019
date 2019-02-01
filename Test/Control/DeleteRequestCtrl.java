package Control;

import Beans.ErrorMsg;
import Beans.RequestBean;
import entity.request.RequestStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteRequestCtrl {

    private static RequestModel requestModel ;
    private static List<RequestBean> list ;

    @BeforeClass
    public  static void setUp(){
        requestModel = new RequestModel("pippo", 1);
        list = requestModel.getAllRequests();
    }

    @Test
    public void deleteRequest(){
        ErrorMsg err;
        for (RequestBean item : list ){
            err = requestModel.setAsClosed(item);
            if (item.getStatus() == RequestStatus.PENDING  || item.getStatus() == RequestStatus.CLOSED)
                assertTrue(err.isErr());
            else {
                list = requestModel.getAllRequests();

                assertFalse(err.isErr());
                err = requestModel.deleteRequest(list.get(list.indexOf(item)));
                if (err.isErr()) System.out.println(err.getMsgList().get(0));
                assertFalse(err.isErr());
            }
        }

    }

}
