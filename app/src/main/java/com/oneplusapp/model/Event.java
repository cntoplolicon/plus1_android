package com.oneplusapp.model;

import org.joda.time.DateTime;

public class Event {
    private int id;
    private String description;
    private DateTime createdAt;
    private EventPage[] eventPages;
    private String logo;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventPage[] getEventPages() {
        return eventPages;
    }

    public void setEventPages(EventPage[] eventPages) {
        this.eventPages = eventPages;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getLogo() {
        return logo;
    }

    public String getTitle() {
        return title;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
