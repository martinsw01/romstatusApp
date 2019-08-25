package no.akademiet.romstatus.fragmants;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import no.akademiet.romstatus.CustomMapListAdapter;
import no.akademiet.romstatus.Listeners.FailureListener;
import no.akademiet.romstatus.MainActivity;
import no.akademiet.romstatus.R;
import no.akademiet.romstatus.Listeners.RefreshListener;
import no.akademiet.romstatus.Room;
import no.akademiet.romstatus.RoomListRequestTask;
import no.akademiet.romstatus.RoomLogic;

public class MapFragment extends Fragment {
    private List<Room> floor1List = new ArrayList<>();
    private List<Room> floor2List = new ArrayList<>();
    private Map map = new Map();

    private List<RefreshListener> refreshListeners = new ArrayList<>();
    private List<FailureListener> failureListeners = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map.showMap(floor1List, 1);
        map.showMap(floor2List, 2);

        refreshOnPull();

    }

    public void setRoomList(List<Room> roomList) {
        RoomLogic.MapLogic mapLogic = RoomLogic.getInstance(getContext(), roomList).new MapLogic();
        mapLogic.setFloorRoomList(roomList);
        floor1List = mapLogic.getFloorOneRoomList();
        floor2List = mapLogic.getFloorTwoRoomList();
    }

    private class Map {
        private void showMap(List<Room> floorList, int floor) {

            int listViewId;
            int parentId;
            switch (floor) {
                case 1:
                    listViewId = R.id.floor1_listMap;
                    parentId = R.id.room_map_floor1;
                    break;
                case 2:
                    listViewId = R.id.floor2_listMap;
                    parentId = R.id.room_map_floor2;
                    break;
                default:
                    listViewId = R.id.floor1_listMap;
                    parentId = R.id.room_map_floor1;
            }

            ListView roomList = getActivity().findViewById(listViewId);

            CustomMapListAdapter adapter = new CustomMapListAdapter(getContext(), getActivity(), R.layout.map_list_layout, parentId, floorList);
            roomList.setAdapter(adapter);

            ConstraintLayout parent = (ConstraintLayout) getActivity().findViewById(parentId);
            ViewGroup.LayoutParams listViewParams = roomList.getLayoutParams();
            listViewParams.height = parent.getMeasuredHeight();
        }



    }

    private void refreshOnPull() {
        final SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.map_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RequestRooms(getContext()).execute();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void notifyRefreshListener() {
        /*for (RefreshListener listener : refreshListeners) {
            listener.notifyRefreshListener(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.AppViews.MAP.id == MainActivity.currentView.id) {
                        setRoomList(RoomLogic.getInstance().getFilteredWithNulledRoomList());
                        map.showMap(floor1List, 1);
                        map.showMap(floor2List, 2);
                    }

                }
            });
        }*/
    }

    private void notifyFailureListener() {
        for (FailureListener listener : failureListeners) {
            listener.notifyFailureListener(R.id.room_map_floor1, MainActivity.AppViews.MAP.id);
        }
    }

    public void addFailureListener(FailureListener listener) {
        this.failureListeners.add(listener);
    }

    public void addRefreshListener(RefreshListener listener){
        this.refreshListeners.add(listener);
    }

    private class RequestRooms extends RoomListRequestTask {

        private RequestRooms(Context context) {
            super(context);
        }

        @Override
        public void doOnSuccess() {
            setRoomList(RoomLogic.getInstance().getFilteredWithNulledRoomList());
            map.showMap(floor1List, 1);
            map.showMap(floor2List, 2);
        }

        @Override
        public void doOnFailure() {
            notifyFailureListener();
        }


    }

}
