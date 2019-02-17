package control;

import beans.ActiveContractBean;
import beans.ErrorMsg;
import beans.RequestBean;
import DAO.ContractDao;
import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import enumeration.UserType;
import enumeration.TypeOfModification;

import java.util.ArrayList;
import java.util.List;

public class InitControl {
    private String userNickname = "";

    public InitControl() {
    }

    public ErrorMsg setUserNickname(String userNickname) {
        ErrorMsg msg = new ErrorMsg();
        if (userNickname!= null && !userNickname.isEmpty())
            this.userNickname = userNickname;
        else
            msg.addMsg("Il nome dell'utente non è corretto\n");
        return msg;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public List<ActiveContractBean> getAllContract(){
        ContractDao dao = ContractDao.getInstance();
        List<ActiveContractBean> list = new ArrayList<>();
        for (UserType type : UserType.values())
            list.addAll(dao.getAllActiveContracts(userNickname, type));
        return list;
    }

    /**
     * ritorna il # di richieste di modifica per un contratto
     */
    public int  getSubmits(ActiveContractBean contract){
        List<RequestBean> list = new ArrayList<>();
        try{
            for (TypeOfModification type : TypeOfModification.values()) {
                RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(type);
                List<RequestBean> tmp = dao.getSubmits(contract.getContractId(), userNickname);
                list.addAll(tmp);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return list.size();
    }
}
