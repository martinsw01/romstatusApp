package no.akademiet.romstatus.fragmants;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import no.akademiet.romstatus.CustomFilterListAdapter;
import no.akademiet.romstatus.R;

public class FiltersRoomFragment extends Fragment {

    String[][] roomList1 = new String[5][2];
    String[][] roomList2 = new String[9][2];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setFilterListAdapter(R.id.filter_room1List, R.id.filter_room1Parent, getRoomList(roomList1, 111));
                setFilterListAdapter(R.id.filter_room2List, R.id.filter_room2Parent, getRoomList(roomList2, 209));
            }
        });

    }


    private void setFilterListAdapter(int listViewId, int parentResource, String [][] roomList) {
        ListView roomListView = getActivity().findViewById(listViewId);
        RoomFilterAdapter adapter = new RoomFilterAdapter(getContext(), getActivity(), R.layout.filter_list_layout, roomList);
        adapter.setParentHeight(adapter.getParentHeight(parentResource));
        roomListView.setAdapter(adapter);
    }

    private String[][] getRoomList(String[][] roomList, int startingRoom) {
        String roomNumber;
        for (int room = 0; room < roomList.length; ++room) {
            roomNumber = String.valueOf(room + startingRoom);
            roomList[room] = new String[] {roomNumber, roomNumber};
        }
        return roomList;
    }

    private class RoomFilterAdapter extends CustomFilterListAdapter {
        public RoomFilterAdapter(Context context, Activity activity, int resource, String[][] objects) {
            super(context, activity, resource, objects);
        }

        @Override
        public int getHeaderHeight(int parentId) {
            return 0;
        }

        @Override
        public ListView getListView() {
            return getActivity().findViewById(R.id.filter_room1List);
        }
    }


}


