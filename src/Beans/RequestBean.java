package Beans;

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

    public RequestBean(TypeOfModification type, Object objectToChange, String reasonWhy, LocalDate date,
                       RequestStatus status, int requestId, String sender) throws IllegalArgumentException{

        ErrorMsg msg = new ErrorMsg();

        msg.addAllMsg(setRequestId(requestId));
        msg.addAllMsg(setSender(sender));
        msg.addAllMsg(setType(type));
        msg.addAllMsg(setObjectToChange(objectToChange));
        setDate(date);
        setReasonWhy(reasonWhy);
        msg.addAllMsg(setStatus(status));

        if (msg.isErr()){
            String err = "";
            for (String str : msg.getMsgList()) {
                err += str;
            }
            throw new IllegalArgumentException(err);
        }
    }

    /**
     * costruttore per richieste a cui non è stato ancora assegnato un id
     */
    public RequestBean(String sender, TypeOfModification type, Object objectToChange,  LocalDate date)
                        throws IllegalArgumentException{
        ErrorMsg msg = new ErrorMsg();
        msg.addAllMsg(setSender(sender));
        msg.addAllMsg(setType(type));
        msg.addAllMsg(setObjectToChange(objectToChange));
        setDate(date);
        //parametri default

        reasonWhy = "";
        status = RequestStatus.PENDING;
        requestId = -1;

        if (msg.isErr()){
            String err = "";
            for (String str : msg.getMsgList()) {
                err += str;
            }
            throw new IllegalArgumentException(err);
        }
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


    public String getSender() {
        return sender;
    }

    public ErrorMsg setSender(String sender) {
        ErrorMsg msg = new ErrorMsg();
        if (sender!= null && !sender.isEmpty())
            this.sender = sender;
        else msg.addMsg("sender nick-name non corretto\n");

        return msg;
    }

    public int getRequestId() {
        return requestId;
    }

    public ErrorMsg setRequestId(int requestId){
        ErrorMsg msg = new ErrorMsg();
        if (requestId < 1) msg.addMsg("Specificare una richiesta esistente\n");
        this.requestId = requestId;

        return msg;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public ErrorMsg setStatus(RequestStatus status){
        ErrorMsg msg = new ErrorMsg();
        if (status == null) msg.addMsg("stato non specificato\n");
        this.status = status;

        return msg;
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

    public ErrorMsg setType(TypeOfModification type) {
        ErrorMsg msg = new ErrorMsg();
        if (type == null) msg.addMsg("tipo di modifica non specificato\n");
        this.type = type;

        return msg;
    }

    public Object getObjectToChange() {
        return objectToChange;
    }

    public ErrorMsg setObjectToChange(Object objectToChange) {
        ErrorMsg msg = new ErrorMsg();
        if (objectToChange == null ) msg.addMsg("specificare l'oggetto della modifica\n");
        this.objectToChange = objectToChange;

        return msg;
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
