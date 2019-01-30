package Beans;

import entity.modification.TypeOfModification;
import entity.request.RequestStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class RequestBean implements Serializable {
    //TODO controllo sui dai!
    private String sender;
    private TypeOfModification type;
    private Object objectToChange;
    private String reasonWhy;
    private LocalDate date;
    private RequestStatus status;
    private int IdRequest;

    public RequestBean(TypeOfModification type, Object objectToChange, String reasonWhy, LocalDate date,
                       RequestStatus status, int idRequest, String sender) {
        this.sender = sender;
        this.type = type;
        this.objectToChange = objectToChange;
        this.reasonWhy = reasonWhy;
        this.date = date;
        this.status = status;
        IdRequest = idRequest;
    }

    public RequestBean(String sender, TypeOfModification type, Object objectToChange, String reasonWhy, LocalDate date) {
        this.sender = sender;
        this.type = type;
        this.objectToChange = objectToChange;
        this.reasonWhy = reasonWhy;
        this.date = date;
        //parametri default

        status = RequestStatus.PENDING;
        IdRequest = -1;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RequestBean that = (RequestBean) object;
        return IdRequest == that.IdRequest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IdRequest);
    }

    public String getSender() {
        return sender;
    }

    public int getIdRequest() {
        return IdRequest;
    }

    public void setIdRequest(int idRequest) {
        IdRequest = idRequest;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TypeOfModification getType() {
        return type;
    }

    public void setType(TypeOfModification type) {
        this.type = type;
    }

    public Object getObjectToChange() {
        return objectToChange;
    }

    public void setObjectToChange(Object objectToChange) {
        this.objectToChange = objectToChange;
    }


    public String getReasonWhy() {
        return reasonWhy;
    }

    public void setReasonWhy(String reasonWhy) {
        this.reasonWhy = reasonWhy;
    }
}
