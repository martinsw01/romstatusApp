package no.akademiet.romstatus.httpRequests;

import android.content.Context;
import android.os.AsyncTask;

import org.springframework.http.HttpEntity;

import java.util.ArrayList;
import java.util.List;

import no.akademiet.romstatus.Room;
import no.akademiet.romstatus.RoomList;
import no.akademiet.romstatus.RoomLogic;
import no.akademiet.romstatus.httpRequests.CustomRestTemplate;


public abstract class RoomListRequestTask extends AsyncTask<Void, Void, RoomList> {
    final private String stringUrl = "http://10.0.0.56:8080";  // TODO: 2019-08-25 set correct url
    final private String prefix = "/object";

    private Context context;


    public RoomListRequestTask(Context context) {
        super();
        this.context = context;
    }

    RoomLogic roomLogic = RoomLogic.getInstance();

    @Override
    protected RoomList doInBackground(Void... voids) {
        try {
            CustomRestTemplate template = new CustomRestTemplate(1000, true);
            HttpEntity<RoomList> entity = template.getForEntity(stringUrl + prefix, RoomList.class);

            return entity.getBody();
        }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    protected void onPostExecute(RoomList roomList) {
        boolean isConnected;
        if (null != roomList) {
            setNewList(roomList);
            doOnSuccess();

            isConnected = true;
        }
        else {
            setDummyList();
            doOnFailure();

            isConnected = false;
        }

        RoomLogic.getInstance().setConnection(isConnected);
    }

    private void setNewList(RoomList roomList) {
        List<Room> newRoomList = new ArrayList<>();
        for (RoomList.Room room : roomList.getRooms()) {
            Room newRoom = new Room();
            newRoom.setRoomNumber(room.getRoomNumber());
            newRoom.setRoomName(room.getRoomName());
            newRoom.setOccupied(room.isRoomOccupied());
            newRoom.setAirQuality(room.getRoomAirQuality());
            newRoom.setFloor(room.getFloor());
            newRoomList.add(newRoom);
        }
        if (null != roomLogic) roomLogic.reset();
        roomLogic = RoomLogic.getInstance(context, newRoomList);
        roomLogic.removeFilteredRooms();
    }

    private void setDummyList() {
        roomLogic = RoomLogic.getInstance(context, getDummyList()); //If roomLogic is not null, meaning a roomList successfully has been requested, no new instance with a dummyList will be created.
        roomLogic.removeFilteredRooms();
    }

    private List<Room> getDummyList() {
        List<Room> dummyRoomList = new ArrayList<>();

        for (int x = 111; x < 115; ++x) {
            dummyRoomList.add(new Room(1, false));
        }

        for (int x = 209; x < 217; ++x) {
            dummyRoomList.add(new Room(2, false));
        }

        return dummyRoomList;
    }

    public abstract void doOnFailure();

    public abstract void doOnSuccess();


}
