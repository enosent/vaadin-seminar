package com.vseminar.data.model;

import java.util.Date;

public class Question {

    private Long id;
    private Long sessionId;
    private String message;
    private Long createBy;
    private Date createDate;

    public Question(){
        this.createDate = new Date();
    }

    public Question(Long sessionId, String message, Long createBy) {
        this.sessionId = sessionId;
        this.message = message;
        this.createBy = createBy;
        this.createDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
