package com.vseminar.data.model;

import java.util.*;

public class Session {

    private static final int MAX_ENTRIES = 100;
    private Set<Long> questions;

    private Long id;
    private String title;
    private LevelType level;
    private String embeddedUrl;
    private Date startDate;
    private Date endDate;
    private Long ownerId;
    private String speaker;
    private String description;

    public Session() {
        this.level = LevelType.Junior;
        this.startDate = new Date();
        this.endDate = new Date();

        newMessage();
    }

    public Session(Long ownerId) {
        this.ownerId = ownerId;

        this.level = LevelType.Junior;
        this.startDate = new Date();
        this.endDate = new Date();

        newMessage();
    }

    public Session(String title, LevelType level, String embeddedUrl, Long ownerId, String speaker, String description) {
        this.title = title;
        this.level = level;
        this.embeddedUrl = embeddedUrl;
        this.startDate = new Date();
        this.endDate = new Date();
        this.ownerId = ownerId;
        this.speaker = speaker;
        this.description = description;

        newMessage();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LevelType getLevel() {
        return level;
    }

    public void setLevel(LevelType level) {
        this.level = level;
    }

    public String getEmbeddedUrl() {
        return embeddedUrl;
    }

    public void setEmbeddedUrl(String embeddedUrl) {
        this.embeddedUrl = embeddedUrl;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private void newMessage() {
        this.questions = Collections.newSetFromMap(new LinkedHashMap<Long, Boolean>(MAX_ENTRIES + 1, .90F, false){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
                return size() > MAX_ENTRIES;
            }
        });
    }

    public Set<Long> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Long> questions) {
        this.questions = questions;
    }
}
