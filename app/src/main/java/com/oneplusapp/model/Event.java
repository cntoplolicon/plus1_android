package com.oneplusapp.model;

import org.joda.time.DateTime;

public class Event {
    private int id;
    private String description;
    private DateTime createdAt;
    private EventPage[] eventPages;

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
}
