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

public class FilterFloorFragment extends Fragment {

    String[] floors = new String[3];


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_floor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                setFilters();
            }
        });
    }

    private String[][] getFloors() {
        floors[0] = getString(R.string.floor_two);
        floors[1] = getString(R.string.floor_one);
        return new String[][] {{floors[0], "2"}, {floors[1], "1"}};
    }

    private void setFilters() {
        ListView floorFilterList = getActivity().findViewById(R.id.filter_floorList);
        FloorFilterAdapter adapter = new FloorFilterAdapter(getContext(), getActivity(), R.layout.filter_list_layout, getFloors());
        adapter.setParentHeight(adapter.getParentHeight(R.id.filter_floorContainer));
        floorFilterList.setAdapter(adapter);
    }


    private class FloorFilterAdapter extends CustomFilterListAdapter {
        public FloorFilterAdapter(Context context, Activity activity, int resource, String[][] objects) {
            super(context, activity, resource, objects);
        }

        @Override
        public int getHeaderHeight(int parentId) {
            return 0;
        }

        @Override
        public ListView getListView() {
            return getActivity().findViewById(R.id.filter_floorList);
        }
    }
}
