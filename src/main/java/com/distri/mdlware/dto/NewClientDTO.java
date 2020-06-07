package com.distri.mdlware.dto;

import uci.middleware.project.dto.Room;

import java.util.Objects;

public class NewClientDTO {

    private String clientId;

    private Room room;

    public NewClientDTO(){

    }

    public NewClientDTO(String clientId, Room room){

        this.clientId = clientId;
        this.room = room;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewClientDTO that = (NewClientDTO) o;
        return clientId.equals(that.clientId) &&
                Objects.equals(room, that.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, room);
    }

    @Override
    public String toString() {
        return "NewClientDTO{" +
                "clientId='" + clientId + '\'' +
                ", room=" + room +
                '}';
    }
}
