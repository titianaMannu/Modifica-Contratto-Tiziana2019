package DAO.modificationDAO;

import Beans.Contract;
import entity.modification.Modification;
import entity.request.RequestForModification;

import java.util.List;

public abstract class RequestForModificationDao {

    abstract boolean updateContract(RequestForModification request);//FATTO !
    abstract boolean insertModification(RequestForModification request);//FATTO!
    abstract boolean deleteModification(RequestForModification request);
    abstract boolean validateModification(RequestForModification request); //FATTO!
    abstract Modification getModification(int idRequest, int idContract);//FATTO!

    /**
     * ritorna una lista di proposte al mittente
     */
    public List<RequestForModification> getSubmits(Contract contract, String sender){

    }

    /**
     *ritorna una lista di richieste per il destinatario
     */
    public List<RequestForModification> getRequests(Contract contract,String receiver){

    }

    public boolean insertRequests(RequestForModification request){

    }

}
