package control;

import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import entity.modification.TypeOfModification;
import entity.request.RequestForModification;
import entity.request.RequestStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * todo 2 thread (lettore, scrittore) che si azionano una volta ogni timeout
 */

public class ExpireControl {
    private Lock lock;
    private List<RequestForModification> list;

    public ExpireControl() {
        this.list = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public void getList(){
        try{
            if (this.lock.tryLock(6, TimeUnit.SECONDS)){
                    this.list.clear();
                    for (TypeOfModification type : TypeOfModification.values()){
                        RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(type);
                        this.list.addAll(dao.getRequestsToExpire());
                    }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.lock.unlock();
        }
    }


    public void analyze(){
        try {
            if (this.lock.tryLock(7, TimeUnit.SECONDS)) {
                for (RequestForModification item : this.list)
                    if (item.getDateOfSubmission().plusDays(5).compareTo(LocalDate.now()) > 0) {
                        // se la richiesta è stata fatta 5 giorni fa ...
                        RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(item.getType());
                        try {
                            item.expire(true);
                            dao.setRequestStatus(item);
                        } catch (SQLException | IllegalStateException e) {
                            e.printStackTrace();
                            //not critical error
                        }
                    }
                //pone le richieste non obsolete nuovamente PENDING
                rollback();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.lock.unlock();
        }
    }


    public void rollback(){
        for (RequestForModification item : this.list){
            if ( item.getStatus().name().equals(RequestStatus.TO_EXPIRE.name()) ){
                item.expire(false);
                RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(item.getType());
                try {
                    dao.setRequestStatus(item);
                } catch (SQLException | IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
