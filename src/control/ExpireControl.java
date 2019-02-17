package control;

import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import enumeration.TypeOfModification;
import entity.RequestForModification;
import enumeration.RequestStatus;

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

    public synchronized boolean buildListToAnalyze(){
        boolean res = false;
        try{
           if (this.lock.tryLock(5, TimeUnit.SECONDS)){
                    this.list.clear();
                    for (TypeOfModification type : TypeOfModification.values()){
                        RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(type);
                        this.list.addAll(dao.getRequestsToExpire());
                    }
                    res = true;
           }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (res)//altrimenti ho rischio di IllegalStateMonitorException
                this.lock.unlock();
            return res;
        }
    }


    public synchronized boolean analyzeRequestToExpire(){
        boolean res = false;
        try {
            if (this.lock.tryLock(5, TimeUnit.SECONDS)) {
                for (RequestForModification item : this.list) {
                    if (item.getDateOfSubmission().plusDays(5).isBefore(LocalDate.now())) {
                        // se la datain cui è stata fatta la richisesta + 5 gg (scadenza) è passata ...
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
            if (res)
                this.lock.unlock();
        }
        return  res;
    }


    private void rollback(){
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
