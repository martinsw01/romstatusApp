package no.akademiet.romstatus;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomList {

    private List<Room> rooms;

    public RoomList() {}

    public RoomList(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public static class Room {

        private int roomAirQuality;
        private int roomNumber;
        private String roomName;
        private boolean roomIsOccupied;
        private int floor;

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }

        public boolean isRoomOccupied() {
            return roomIsOccupied;
        }

        public void setRoomOccupied(boolean roomIsOccupied) {
            this.roomIsOccupied = roomIsOccupied;
        }

        public int getRoomAirQuality() {
            return roomAirQuality;
        }

        public void setRoomAirQuality(int roomAirQuality) {
            this.roomAirQuality = roomAirQuality;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(int roomNumber) {
            this.roomNumber = roomNumber;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "RoomList{" + "rooms='" + rooms + '}';
    }
}