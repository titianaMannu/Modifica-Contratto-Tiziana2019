package view.init_page;

import entity.ActiveContract;
import Beans.ErrorMsg;
import Beans.RequestBean;
import DAO.ContractDao;
import DAO.modificationDAO.ModificationDaoFActory;
import DAO.modificationDAO.RequestForModificationDao;
import entity.UserType;
import entity.modification.TypeOfModification;

import java.util.ArrayList;
import java.util.List;

public class InitModel {
    private String userNickname = "";

    public InitModel() {
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

    public List<ActiveContract> getAllContract(){
        ContractDao dao = ContractDao.getInstance();
        List<ActiveContract> list = new ArrayList<>();
        for (UserType type : UserType.values())
            list.addAll(dao.getAllActiveContracts(userNickname, type));
        return list;
    }

    /**
     * ritorna il # di richieste di modifica per un contratto
     */
    public int  getSubmits(ActiveContract contract){
        List<RequestBean> list = new ArrayList<>();
        try{
            for (TypeOfModification type : TypeOfModification.values()) {
                RequestForModificationDao dao = ModificationDaoFActory.getInstance().createProduct(type);
                List<RequestBean> tmp = dao.getSubmits(contract, userNickname);
                list.addAll(tmp);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return list.size();
    }
}
