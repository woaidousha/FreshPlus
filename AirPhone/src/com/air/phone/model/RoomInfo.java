package com.air.phone.model;

public class RoomInfo {

    private String roomName;
    private String roomArea;

    public RoomInfo(String roomName, String roomArea) {
        this.roomName = roomName;
        this.roomArea = roomArea;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomArea() {
        return roomArea;
    }

    public void setRoomArea(String roomArea) {
        this.roomArea = roomArea;
    }
}
