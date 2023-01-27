package com.acework.shabaretailer.model;

public class EmailChangeRequest {
    private String id, uid, email, status, code, reason;
    private long timestamp;

    public EmailChangeRequest() {
    }

    public EmailChangeRequest(String uid, String email, String status, String code, String reason, long timestamp) {
        this.uid = uid;
        this.email = email;
        this.status = status;
        this.code = code;
        this.reason = reason;
        this.timestamp = timestamp;
        id = uid + timestamp;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
