package no.akademiet.romstatus.fragmants;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import no.akademiet.romstatus.CustomRoomListAdapter;
import no.akademiet.romstatus.Listeners.FailureListener;
import no.akademiet.romstatus.MainActivity;
import no.akademiet.romstatus.R;
import no.akademiet.romstatus.Listeners.RefreshListener;
import no.akademiet.romstatus.Room;
import no.akademiet.romstatus.RoomListRequestTask;
import no.akademiet.romstatus.RoomLogic;

public class TableFragment extends Fragment {
    private boolean roomStatus = true;
    private int roomCO2PPM = 0;

    private List<Room> roomList = new ArrayList<>();

    Table table = new Table();

    private List<RefreshListener> refreshListeners = new ArrayList<>();
    private List<FailureListener> failureListeners = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        table.showTable(roomList);

        table.setOnClickListeners();

        refreshOnPull();

    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public void setRoomStatus(boolean roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void setRoomCO2PPM(int roomCO2PPM) {
        this.roomCO2PPM = roomCO2PPM;
    }

    public void editRoomList(List<Room> rooms, int position) {
        rooms.get(position).setOccupied(roomStatus);
        rooms.get(position).setAirQuality(roomCO2PPM);
    }



    private class Table {
        private void showTable(List<Room> roomList) {
            ListView rowList = getActivity().findViewById(R.id.room_list);
            CustomRoomListAdapter adapter = new CustomRoomListAdapter(getContext(), getActivity(), R.layout.table_row_layout, roomList);
            rowList.setAdapter(adapter);
            CustomRoomListAdapter.setListViewHeightBasedOnChildren(rowList);
        }


        class SortByRoomNumber implements Comparator<Room> {
            @Override
            public int compare(Room room1, Room room2) {
                return room1.getRoomNumber() - room2.getRoomNumber();
            }
        }
        class SortByRoomName implements Comparator<Room> {
            @Override
            public int compare(Room room1, Room room2) {
                return room1.getRoomName().compareTo(room2.getRoomName());
            }
        }
        class SortByRoomStatus implements Comparator<Room> {
            @Override
            public int compare(Room room1, Room room2) {
                int intValueOfRoom1 = room1.isOccupied() ? 1 : 0;
                int intValueOfRoom2 = room2.isOccupied() ? 1 : 0;
                return intValueOfRoom1 - intValueOfRoom2;
            }
        }
        class SortByRoomAirQuality implements Comparator<Room> {
            @Override
            public int compare(Room room1, Room room2) {
                return room1.getCO2PPM() - room2.getCO2PPM();
            }
        }

        private void onHeaderClick(Comparator<Room> comparator) {//int headerId) {
            Collections.sort(roomList, comparator);
            showTable(roomList);

        }

        private void setOnClickListeners() {
            TextView roomNumberHeaderField = (TextView) getActivity().findViewById(R.id.roomNumberHeader);
            TextView roomNameHeaderField = (TextView) getActivity().findViewById(R.id.roomNameHeader);
            TextView roomStatusHeaderField = (TextView) getActivity().findViewById(R.id.roomStatusHeader);
            TextView roomAirQualityHeaderField = (TextView) getActivity().findViewById(R.id.roomAirQualityHeader);

            roomNumberHeaderField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHeaderClick(new SortByRoomNumber());
                }
            });

            roomNameHeaderField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHeaderClick(new SortByRoomName());
                }
            });

            roomStatusHeaderField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHeaderClick(new SortByRoomStatus());
                }
            });

            roomAirQualityHeaderField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHeaderClick(new SortByRoomAirQuality());
                }
            });
        }

    }

    private void refreshOnPull() {
        final SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.table_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RequestRooms(getContext()).execute();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void notifyRefreshListener() {
        for (RefreshListener listener : refreshListeners) {
            listener.notifyRefreshListener(R.id.table_head, MainActivity.AppViews.TABLE.id);

            /*listener.notifyRefreshListener(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.AppViews.TABLE.id == MainActivity.currentView.id)
                        table.showTable(RoomLogic.getInstance().getFilteredRoomList());
                }
            });*/
        }
    }

    private void notifyFailureListener() {
        for (FailureListener listener : failureListeners) {
            listener.notifyFailureListener(R.id.table_head, MainActivity.AppViews.TABLE.id);
        }
    }

    public void addFailureListener(FailureListener listener) {
        this.failureListeners.add(listener);
    }

    public void addRefreshListener(RefreshListener listener){
        this.refreshListeners.add(listener);
    }


    private class RequestRooms extends RoomListRequestTask {

        public RequestRooms(Context context) {
            super(context);
        }

        @Override
        public void doOnSuccess() {
            table.showTable(RoomLogic.getInstance().getFilteredRoomList());
        }

        @Override
        public void doOnFailure() {
            notifyFailureListener();
        }


    }
}
