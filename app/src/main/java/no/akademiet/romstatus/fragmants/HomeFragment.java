package no.akademiet.romstatus.fragmants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import no.akademiet.romstatus.Listeners.FailureListener;
import no.akademiet.romstatus.Listeners.FilterFragmentListener;
import no.akademiet.romstatus.MainActivity;
import no.akademiet.romstatus.R;
import no.akademiet.romstatus.httpRequests.RoomListRequestTask;
import no.akademiet.romstatus.RoomLogic;
import no.akademiet.romstatus.httpRequests.VersionRequestTask;

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

        String filterCount;
        String roomCount;

        boolean isConnected = RoomLogic.getInstance().isConnected();

        roomCount = isConnected ? String.valueOf(RoomLogic.getInstance().getEmptyRoomCount()) : "N/A";
        filterCount = String.valueOf(RoomLogic.FilterLogic.getInstance().getFilterCunt(getContext()));

        setCount(R.id.filtersAmount, filterCount);
        setCount(R.id.empty_rooms_number, roomCount);

        checkForUpdates();

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

    private void setCount(int textViewId, String count) {
        TextView countField = (TextView) getActivity().findViewById(textViewId);

        if (null != countField) {
            countField.setText(count);
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
            setCount(R.id.empty_rooms_number, String.valueOf(RoomLogic.getInstance().getEmptyRoomCount()));
            CustomSnackbar.make(getContext(), getActivity()).show();
        }

        @Override
        public void doOnFailure() {
            notifyFailureListener();
        }
    }

    private void checkForUpdates() {
        CardView updatesCard = (CardView) getActivity().findViewById(R.id.updates_card);
        updatesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestVersion(getContext()).execute();
            }
        });
    }

    private class RequestVersion extends VersionRequestTask {
        public RequestVersion(Context context) {
            super(context);
        }

        @Override
        public void doIfTrue() {
            Snackbar.make(getActivity().findViewById(R.id.content), getContext().getString(R.string.latestVersionTrue), Snackbar.LENGTH_SHORT);
        }

        @Override
        public void doOnFailure() {
            notifyFailureListener();
        }
    }



}
