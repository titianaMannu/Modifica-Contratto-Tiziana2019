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



public class ExpireControl {
    private Lock lock;
    private List<RequestForModification> list;

    private ExpireControl() {
        this.list = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    private static class LazyContainer{
        private final static ExpireControl control = new ExpireControl();
    }

    public static ExpireControl getInstance(){
        return LazyContainer.control;
    }

    public boolean buildList(){
        boolean res = false;
        try{
            if (this.lock.tryLock(6, TimeUnit.SECONDS)){
                    this.list.clear();
                    for (TypeOfModification type : TypeOfModification.values()){
                        RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(type);
                        this.list.addAll(dao.getRequestsToExpire());
                    }
            }
            res = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.lock.unlock();
            return res;
        }
    }


    public boolean analyze(){
        boolean res = false;
        try {
            if (this.lock.tryLock(7, TimeUnit.SECONDS)) {
                for (RequestForModification item : this.list ){
                    if (item.getDateOfSubmission().plusDays(5).isAfter(LocalDate.now())) {
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
                }
                res = true;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //pone le richieste non obsolete nuovamente PENDING
            rollback();
            this.lock.unlock();
            return  res;
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
