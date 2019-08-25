package no.akademiet.romstatus.fragmants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.akademiet.romstatus.Listeners.FailureListener;
import no.akademiet.romstatus.Listeners.FilterFragmentListener;
import no.akademiet.romstatus.MainActivity;
import no.akademiet.romstatus.R;
import no.akademiet.romstatus.RoomListRequestTask;
import no.akademiet.romstatus.RoomLogic;

public class HomeFragment extends Fragment {

    List<FilterFragmentListener> filterFragmentListeners = new ArrayList<>();
    List<FailureListener> failureListeners = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setNightModeIcon();
        //doOnClick(R.id.filter_card, goToFilters);
        doOnClick(R.id.nightMode_card, setTheme);
        setCount(R.id.filtersAmount, RoomLogic.FilterLogic.getInstance().getFilterCunt(getContext()));
        setCount(R.id.empty_rooms_number, RoomLogic.getInstance().getFilteredRoomList().size());

        refreshOnPull();
    }

    private void doOnClick(int cardViewId, final Runnable method) {
        CardView cardView = getActivity().findViewById(cardViewId);
        if (null != cardView) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    method.run();
                }
            });
        }
    }


    private Runnable setTheme = new Runnable() {
        @Override
        public void run() {
            SharedPreferences preferences = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            boolean darkModeIsEnabled = preferences.getBoolean("DarkMode", false);
            editor.putBoolean("DarkMode", !darkModeIsEnabled);
            editor.apply();
            getActivity().recreate();


        }
    };

    private void setCount(int textViewId, int count) {
        TextView countField = (TextView) getActivity().findViewById(textViewId);

        if (null != countField) {
            countField.setText(String.valueOf(count));
        }
    }


    private void setNightModeIcon() {
        boolean nightModeActive = getContext().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE)
                .getBoolean("DarkMode", false);

        TextView nightModeField = (TextView) getActivity().findViewById(R.id.nightMode_text);
        ImageView nightModeIcon = (ImageView) getActivity().findViewById(R.id.nightMode_icon);

        String nightModeText;
        int path;

        if (nightModeActive) {
            nightModeText = getString(R.string.day_mode);
            path = R.drawable.ic_baseline_wb_sunny_24px;
        }
        else {
            nightModeText = getString(R.string.night_mode);
            path = R.drawable.ic_baseline_brightness_3_24px;
        }

        nightModeField.setText(nightModeText);
        nightModeIcon.setImageResource(path);

    }

    private void refreshOnPull() {
        final SwipeRefreshLayout refreshLayout = getActivity().findViewById(R.id.home_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RequestRooms(getContext()).execute();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void notifyFailureListener() {
        for (FailureListener listener : failureListeners) {
            listener.notifyFailureListener(R.id.filter_card, MainActivity.AppViews.HOME.id);
        }
    }

    public void addFailureListener(FailureListener listener) {
        failureListeners.add(listener);
    }

    private class RequestRooms extends RoomListRequestTask {

        public RequestRooms(Context context) {
            super(context);
        }

        @Override
        public void doOnSuccess() {
            setCount(R.id.filtersAmount, RoomLogic.FilterLogic.getInstance().getFilterCunt(getContext()));
        }

        @Override
        public void doOnFailure() {
            notifyFailureListener();
        }
    }
}
