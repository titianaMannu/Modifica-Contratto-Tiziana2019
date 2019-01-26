package DAO.modificationDAO;

import Beans.Contract;
import entity.modification.Modification;
import entity.request.RequestForModification;
import java.util.List;

public interface RequestForModificationDao {

    /**
     * Applica la modifica contenuta nella richiesta al contratto
     * @param request : richiesta di modifica
     * @return true se la transazione fa commit, false altrimenti
     */
    boolean updateContract(RequestForModification request);

    /**
     * inserisce la modifica nel DB
     */
    boolean insertModification(RequestForModification request);

    /**
     * controlla che non ci siano altre richieste di modifica uguali (PENDING) per il contratto
     * @return true se la  richiesta di modifica è applicabile, false altrimenti
     */
    boolean validateModification(RequestForModification request);

    Modification getModification(int contractId, int requestId);

    /**
     * Si occupa dell'eliminazione della richiesta (politica di eliminazione a cascata per la modifica corrispondente)
     * @param request : richiesta da modificare
     * @return true se la transazione si è conclusa con successo, false altrimenti
     */
    boolean deleteRequest(RequestForModification request);

    /**
     @return : una lista contenete tutte le richieste  relative contrat e inviate da sender
     */
    List<RequestForModification> getSubmits(Contract contract, String sender);

    /**
     *@return una lista contenete tutte le richieste  relative contrat e destinate a receiver
     */
    List<RequestForModification> getRequests(Contract contract, String receiver);

    /**
     * Si occupa dell'inserimento della richiesta e della modifica corrispondente
     * @return : true se la transazione si conclude con successo, false altrimenti
     */
    boolean insertRequests(RequestForModification request);

}
