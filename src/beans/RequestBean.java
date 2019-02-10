package beans;

import entity.modification.TypeOfModification;
import entity.request.RequestStatus;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * RequestBean si occupa di incapsulare i propri dati e la loro logica di controllo
 */
public class RequestBean implements Serializable {
    private String sender;
    private TypeOfModification type;
    private Object objectToChange;
    private String reasonWhy;
    private LocalDate date;
    private RequestStatus status;
    private int requestId;
    private ErrorMsg msg = new ErrorMsg();

    public RequestBean(TypeOfModification type, Object objectToChange, String reasonWhy, LocalDate date,
                       RequestStatus status, int requestId, String sender) {

        setRequestId(requestId);
        setSender(sender);
        setType(type);
        setObjectToChange(objectToChange);
        setDate(date);
        setReasonWhy(reasonWhy);
        setStatus(status);

    }

    /**
     * costruttore per richieste a cui non è stato ancora assegnato un id
     */
    public RequestBean(String sender, TypeOfModification type, Object objectToChange,  LocalDate date) {
        setSender(sender);
        setType(type);
        setObjectToChange(objectToChange);
        setDate(date);
        //parametri default

        reasonWhy = "";
        status = RequestStatus.PENDING;
        requestId = -1;

    }


    public RequestBean() {
        //Bean deve avere un costruttore di default
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RequestBean that = (RequestBean) object;
        return requestId == that.requestId;
    }


    public boolean isValid(){
        return !msg.isErr();
    }

    public ErrorMsg getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        if (sender!= null && !sender.isEmpty())
            this.sender = sender;
        else msg.addMsg("sender nick-name non corretto\n");
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId){
        if (requestId < 1) msg.addMsg("Specificare una richiesta esistente\n");
        this.requestId = requestId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status){
        if (status == null) msg.addMsg("stato non specificato\n");
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null)
            this.date = LocalDate.now();
        else
            this.date = date;
    }

    public TypeOfModification getType() {
        return type;
    }

    public void setType(TypeOfModification type) {
        if (type == null) msg.addMsg("tipo di modifica non specificato\n");
        this.type = type;
    }

    public Object getObjectToChange() {
        return objectToChange;
    }

    public void setObjectToChange(Object objectToChange) {
        if (objectToChange == null ) msg.addMsg("specificare l'oggetto della modifica\n");
        this.objectToChange = objectToChange;
    }


   public String getReasonWhy() {
        return reasonWhy;
    }

    public void setReasonWhy(String reasonWhy) {
        if (reasonWhy != null && !reasonWhy.isEmpty())
            this.reasonWhy = reasonWhy;
        else
            this.reasonWhy = "";
    }

}
