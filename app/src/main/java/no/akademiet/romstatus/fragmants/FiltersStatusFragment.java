package no.akademiet.romstatus.fragmants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog.Builder;

import no.akademiet.romstatus.CustomFilterListAdapter;
import no.akademiet.romstatus.CustomRoomListAdapter;
import no.akademiet.romstatus.R;

public class FiltersStatusFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                String[][] statusFilters = new String[][] {{getString(R.string.notOccupied), "false"},{ getString(R.string.occupied), "true"}};
                setCustomListFilterAdapter(statusFilters, R.id.status_filter_list, R.id.filter_status_box);

                String[][] airQualityFilters = new String[][] {{getString(R.string.greatQuality), getString(R.string.greatQuality)},{getString(R.string.mediumQuality), getString(R.string.mediumQuality)}, {getString(R.string.poorQuality), getString(R.string.poorQuality)}};
                setCustomListFilterAdapter(airQualityFilters, R.id.airQuality_filter_list, R.id.filter_airQuality_box);
            }
        });

    }

    private void setCustomListFilterAdapter(String[][] filters, int resourceId, int parentResourceId) {
        ListView statusFilterGrid = (ListView) getActivity().findViewById(resourceId);
        StatusFilterAdapter statusAdapter = new StatusFilterAdapter(getContext(), getActivity(), R.layout.filter_list_layout,filters);
        statusAdapter.setParentHeight(statusAdapter.getParentHeight(parentResourceId));
        statusFilterGrid.setAdapter(statusAdapter);

        ViewGroup.LayoutParams params = statusFilterGrid.getLayoutParams();
        params.height = statusAdapter.getParentHeight(parentResourceId) * 3/2;
        statusFilterGrid.setLayoutParams(params);
        statusFilterGrid.requestLayout();
    }

    private class StatusFilterAdapter extends CustomFilterListAdapter {
        public StatusFilterAdapter(Context context, Activity activity, int resource, String[][] objects) {
            super(context, activity, resource, objects);
        }

        @Override
        public int getHeaderHeight(int parentId) {
            ConstraintLayout parent = (ConstraintLayout) getActivity().findViewById(parentId);
            TextView firstHeader = (TextView) parent.getChildAt(0);
            ListView listView = (ListView) parent.getChildAt(1);
            return firstHeader.getMeasuredHeight() + listView.getPaddingTop();
        }

        @Override
        public ListView getListView() {
            return getActivity().findViewById(R.id.status_filter_list);
        }
    }
}
