package entity;

import entity.modification.Modification;

import java.time.LocalDate;

public class RequestForModification {
    private RequestStatus status;
    private String senderNickname;
    private String receiverNickname;
    private LocalDate dateOfSubmission;
    private long contractId;
    private Modification modification;

}
