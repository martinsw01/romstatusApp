package no.akademiet.romstatus;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class RoomLogic {

    private Context context;
    private List<Room> fullRoomList;
    private List<Room> filteredRoomList;

    private static RoomLogic single_instance = null;

    private Boolean isConnected;

    private RoomLogic(Context context, List<Room> fullRoomList) {
        this.context = context;
        this.fullRoomList = fullRoomList;
    }

    public static RoomLogic getInstance(Context context, List<Room> roomList) {
        if (null == single_instance) {
            single_instance = new RoomLogic(context, roomList);
        }
        return single_instance;
    }

    public static RoomLogic getInstance() {
        return single_instance;
    }

    public void reset() {
        single_instance = null;
    }

    public void setConnection(boolean isConnected) {
        if (null == this.isConnected)
            this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<Room> getFullRoomList() {
        return fullRoomList;
    }

    public int getEmptyRoomCount() {
        int emptyRooms = 0;
        for (Room room : fullRoomList) {
            if (!room.isOccupied()) {
                ++emptyRooms;
            }
        }
        return emptyRooms;
    }

    public void setFullRoomList(List<Room> fullRoomList) {
        this.fullRoomList = fullRoomList;
    }

    public void removeFilteredRooms() {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String floorKey;
        String statusKey;
        String airQualKey;
        String roomNumKey;

        boolean isFloorActive;
        boolean isStatusActive;
        boolean isAirQualActive;
        boolean isRoomNumActive;

        Room room;

        List<Room> filteredRoomList = new ArrayList<>();
        filteredRoomList.addAll(fullRoomList);

        List<Room> excludedRoomList = new ArrayList<>();

        for (int x = 0; x < fullRoomList.size(); ++x) {
            room = fullRoomList.get(x);

            floorKey = String.valueOf(room.getFloor());
            statusKey = String.valueOf(room.isOccupied());
            airQualKey = context.getString(room.getAirQuality());
            roomNumKey = String.valueOf(room.getRoomNumber());

            isFloorActive = preferences.getBoolean(floorKey, true);
            isStatusActive = preferences.getBoolean(statusKey, true);
            isAirQualActive = preferences.getBoolean(airQualKey, true);
            isRoomNumActive = preferences.getBoolean(roomNumKey, true);

            if (!isFloorActive || !isStatusActive || !isAirQualActive || !isRoomNumActive) {
                filteredRoomList.remove(room);
                excludedRoomList.add(room);
            }
        }
        this.filteredRoomList = filteredRoomList;
    }

    public List<Room> getFilteredWithNulledRoomList() {
        List<Room> filteredWithNulledRoomList = new ArrayList<>(filteredRoomList);
        filteredWithNulledRoomList.add(new Room(1, false));

        int roomNumberInFullList;
        int roomNumberInFilteredList;

        try {
            for (int x = 0; x < fullRoomList.size(); ++x) {
                roomNumberInFullList = fullRoomList.get(x).getRoomNumber();
                roomNumberInFilteredList = filteredWithNulledRoomList.get(x).getRoomNumber();

                if (roomNumberInFilteredList != roomNumberInFullList) {
                    filteredWithNulledRoomList.add(x, new Room(fullRoomList.get(x).getFloor(), true));
                }
            }
        }
        catch (Exception e) {e.printStackTrace();}

        return filteredWithNulledRoomList;
    }

    public List<Room> getFilteredRoomList() {
        return filteredRoomList;
    }

    public static class FilterLogic {

        private FilterLogic() {
        }

        private static FilterLogic single_instance = null;

        public static FilterLogic getInstance() {
            if (null == single_instance) {
                single_instance = new FilterLogic();
            }
            return single_instance;
        }

        private boolean[] filtersActive = new boolean[21];  // 0 - 1: floors    2 - 3: status   4 - 6: air quality  7 - 20: room number

        public void setFilterActive(boolean filterActive, int filter) {
            filtersActive[filter] = filterActive;
        }

        public boolean isAnyFilterActive() {
            for (boolean filterActive : filtersActive) {
                if (Boolean.TRUE.equals(filterActive)) {
                    return true;
                }
            }
            return false;
        }


        public int getFilterCunt(Context context) {
            String[] filters = new String[] {
                    "1",
                    "2",
                    "false",
                    "true",
                    context.getString(R.string.greatQuality),
                    context.getString(R.string.mediumQuality),
                    context.getString(R.string.poorQuality),
                    "111",
                    "112",
                    "113",
                    "114",
                    "111",
                    "209",
                    "210",
                    "211",
                    "212",
                    "213",
                    "214",
                    "215",
                    "216",
                    "217"
            };

            SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            boolean filterActive;

            int filterCount = 0;
            for (String filterKey : filters) {
                filterActive = preferences.getBoolean(filterKey, false);
                if (!filterActive)
                    ++filterCount;
            }
            return filterCount;
        }

    }

    public class MapLogic {
        private List<Room> floorOneRoomList = new ArrayList<>();
        private List<Room> floorTwoRoomList = new ArrayList<>();

        public void setFloorRoomList(List<Room> roomList) {
            int floor;
            for (Room room : roomList) {
                floor = room.getFloor();
                if (1 == floor) {
                    floorOneRoomList.add(room);
                }
                else
                    floorTwoRoomList.add(room);
            }
        }

        public List<Room> getFloorOneRoomList() {
            return floorOneRoomList;
        }

        public List<Room> getFloorTwoRoomList() {
            return floorTwoRoomList;
        }
    }
}
