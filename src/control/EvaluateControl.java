package control;

import entity.ActiveContract;
import beans.ErrorMsg;
import beans.RequestBean;
import DAO.ContractDao;
import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import enumeration.TypeOfModification;
import entity.RequestForModification;
import enumeration.RequestStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ottieni proposte
 * accetta proposte
 * respingi proposte
 */

public class EvaluateControl {
    private String userNickname;
    private ActiveContract activeContract;

    public EvaluateControl(String userNickname, int contractId) {
        setUserNickname(userNickname);
        setActiveContract(contractId);
    }

    public ErrorMsg setUserNickname(String userNickname) {
        ErrorMsg msg = new ErrorMsg();
        if (userNickname!= null && !userNickname.isEmpty())
            this.userNickname = userNickname;
        else
            msg.addMsg("Il nome dell'utente non è corretto\n");
        return msg;
    }

    public ErrorMsg setActiveContract(int contractId) {
        ErrorMsg msg = new ErrorMsg();
        ContractDao dao = ContractDao.getInstance();
        ActiveContract activeContract = dao.getContract(contractId);
        if (activeContract == null)
            msg.addMsg("Il contratto selezionato non è stato trovato\nPotrebbe non essere più attivo\n");
        else if (!(activeContract.getRenterNickname().equals(userNickname) ||
                activeContract.getTenantNickname().equals(userNickname)) )
            msg.addMsg("UserName e/o codice contratto non compatibili\n");
        else
            this.activeContract = activeContract;
        return msg;
    }

    public List<RequestBean> getSubmits(){
        List<RequestBean> list = new ArrayList<>();
        try{
            for (TypeOfModification type : TypeOfModification.values()) {
                RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(type);
                List<RequestBean> tmp = dao.getSubmits(activeContract.getContractId(), userNickname);
                list.addAll(tmp);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return list;
    }

    public ErrorMsg accept(RequestBean requestBean){
        ErrorMsg msg = new ErrorMsg();
        if (requestBean.getStatus() != RequestStatus.PENDING){
            msg.addMsg("Solo richieste pending possono essere accettate.\n");
            return msg;
        }
        try{
            RequestForModification request = new RequestForModification(requestBean.getRequestId(),
                    activeContract, requestBean.getType(),requestBean.getObjectToChange(), userNickname,
                    requestBean.getReasonWhy(),requestBean.getDate(), requestBean.getStatus());

            System.out.println(this.activeContract.toString());
            request.accept();
            System.out.println(this.activeContract.toString());
            RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(requestBean.getType());
            dao.updateContract(request);
            //aggiorno lo stato della richiesta
            dao.setRequestStatus(request);
        }catch(SQLException | NullPointerException e){
            msg.addMsg("Operazione non riuscita: " + e.getMessage());

        } catch (IllegalStateException | IllegalArgumentException e) {
            msg.addMsg(e.getMessage());
        }

        return msg;
    }


    public ErrorMsg decline(RequestBean requestBean){
        ErrorMsg msg = new ErrorMsg();
        if (requestBean.getStatus() != RequestStatus.PENDING){
            msg.addMsg("Solo richieste pending possono essere respinte\n");
            return msg;
        }
        try{
            RequestForModification request = new RequestForModification(requestBean.getRequestId(),
                    activeContract, requestBean.getType(),requestBean.getObjectToChange(), userNickname,
                    requestBean.getReasonWhy(),requestBean.getDate(), requestBean.getStatus());

            request.decline();
            RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(requestBean.getType());
            //aggiorno lo stato della richiesta
            dao.setRequestStatus(request);

        }catch(SQLException | NullPointerException e){
            msg.addMsg("Operazione non riuscita: " + e.getMessage());

        } catch (IllegalStateException | IllegalArgumentException e) {
            msg.addMsg(e.getMessage());
        }

        return msg;
    }


}
