package com.distri.mdlware.websocket;

public class TimingEvent {

	public String clientID;
	public String roomName;
	public String currTimeInSecs;

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getCurrTimeInSecs() {
		return currTimeInSecs;
	}

	public void setCurrTimeInSecs(String currTimeInSecs) {
		this.currTimeInSecs = currTimeInSecs;
	}

}
