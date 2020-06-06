package com.distri.mdlware.websocket;

public class TimingEvent {

	public String clientID;
	public String roomName;
	public String streamPosition;
	public String positionSnapshotTime;

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

	public String getStreamPosition() {
		return streamPosition;
	}

	public void setStreamPosition(String streamPosition) {
		this.streamPosition = streamPosition;
	}

	public String getPositionSnapshotTime() {
		return positionSnapshotTime;
	}

	public void setPositionSnapshotTime(String positionSnapshotTime) {
		this.positionSnapshotTime = positionSnapshotTime;
	}
}
