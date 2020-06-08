package com.distri.mdlware.dto;

import uci.middleware.project.dto.Room;

import java.util.Objects;

public class NewClientDTO {

    private String clientID;

    private Room room;

    public NewClientDTO(){

    }

    public NewClientDTO(String clientID, Room room){

        this.clientID = clientID;
        this.room = room;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
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
        return clientID.equals(that.clientID) &&
                Objects.equals(room, that.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientID, room);
    }

    @Override
    public String toString() {
        return "NewClientDTO{" +
                "clientId='" + clientID + '\'' +
                ", room=" + room +
                '}';
    }
}
