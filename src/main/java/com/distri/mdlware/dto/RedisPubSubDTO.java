package com.distri.mdlware.dto;

import com.distri.mdlware.websocket.Event;

public class RedisPubSubDTO {

    private String sender;
    private String room;
    private Event event;

    public RedisPubSubDTO() {

    }

    public RedisPubSubDTO(String sender, String room, Event event) {
        this.sender = sender;
        this.room = room;
        this.event = event;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "RedisPubSubDTO{" +
                "sender='" + sender + '\'' +
                ", room='" + room + '\'' +
                ", event=" + event +
                '}';
    }
}
