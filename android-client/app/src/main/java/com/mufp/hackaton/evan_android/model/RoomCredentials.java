package com.mufp.hackaton.evan_android.model;

/**
 * Created by milan on 23.11.14..
 */
public class RoomCredentials {
    private String roomName;
    private String roomPassword;

    public RoomCredentials(String roomName, String roomPassword) {
        super();
        this.roomName = roomName;
        this.roomPassword = roomPassword;
    }

    public RoomCredentials(){
        super();
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }
}
