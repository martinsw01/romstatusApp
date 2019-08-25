package no.akademiet.romstatus;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


public abstract class RoomListRequestTask extends AsyncTask<Void, Void, RoomList> {
    final private String stringUrl = "http://10.0.0.135:8080";  // TODO: 2019-08-25 set correct url
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
        if (null != roomList) {
            setNewList(roomList);
            doOnSuccess();
        }
        else {
            setDummyList();
            doOnFailure();
        }
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

        dummyRoomList.add(new Room(1));
        dummyRoomList.add(new Room(1));
        dummyRoomList.add(new Room( 1));
        dummyRoomList.add(new Room( 1));
        dummyRoomList.add(new Room( 1));

        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));
        dummyRoomList.add(new Room( 2));

        return dummyRoomList;
    }

    public abstract void doOnFailure();

    public abstract void doOnSuccess();


}
