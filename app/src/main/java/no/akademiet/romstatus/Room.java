package no.akademiet.romstatus;

public class Room {
    private int roomNumber;
    private String roomName;
    private boolean isOccupied;
    private int airQuality;
    private int floor;

    public Room(int roomNumber, String roomName, boolean isOccupied, int airQuality, int floor) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        this.isOccupied = isOccupied;
        this.airQuality = airQuality;
        this.floor = floor;
    }

    public Room(int floor, boolean filtered) {
        this.floor = floor;
        this.roomNumber = filtered ? -1 : 0;
    }

    public Room() {}

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

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public int getCO2PPM() {
        return airQuality;
    }

    public int getAirQuality() {
        if (airQuality < 500) {
            return R.string.greatQuality;
        }
        else if (airQuality < 700) {
            return R.string.mediumQuality;
        }
        return R.string.poorQuality;
    }

    public void setAirQuality(int airQuality) {
        this.airQuality = airQuality;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
