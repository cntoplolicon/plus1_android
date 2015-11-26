package com.oneplusapp.model;

/**
 * Created by cntoplolicon on 11/26/15.
 */
public class Event {
    private int id;
    private String description;
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
}
