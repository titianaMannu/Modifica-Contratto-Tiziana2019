package Beans;

import entity.modification.TypeOfModification;
import entity.request.RequestStatus;

import java.io.Serializable;
import java.time.LocalDate;

public class RequestBean implements Serializable {
    //TODO controllo sui dai!
    private TypeOfModification type;
    private Object objectToChange;
    private String reasonWhy;
    private LocalDate date;
    private RequestStatus status;
    private int IdRequest;

    public RequestBean(TypeOfModification type, Object objectToChange, String reasonWhy, LocalDate date,
                       RequestStatus status, int idRequest) {
        this.type = type;
        this.objectToChange = objectToChange;
        this.reasonWhy = reasonWhy;
        this.date = date;
        this.status = status;
        IdRequest = idRequest;
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
