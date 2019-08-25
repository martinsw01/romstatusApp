package no.akademiet.romstatus;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import no.akademiet.romstatus.Listeners.FailureListener;
import no.akademiet.romstatus.fragmants.FiltersMainFragment;
import no.akademiet.romstatus.fragmants.HomeFragment;
import no.akademiet.romstatus.fragmants.MapFragment;
import no.akademiet.romstatus.fragmants.TableFragment;

public class MainActivity extends AppCompatActivity implements FailureListener {


    final public static String FRAGMENT_TABLE = "frag_table";
    final public static String FRAGMENT_HOME = "frag_home";
    final public static String FRAGMENT_MAP = "frag_map";
    final public static String FRAGMENT_FILTER = "frag_filter";
    final public static String FRAGMENT_LOADING = "frag_loading";


    public static AppViews currentView = AppViews.TABLE;

    TableFragment tableFragment = new TableFragment();
    HomeFragment homeFragment = new HomeFragment();
    FiltersMainFragment filterFragment = new FiltersMainFragment();
    MapFragment mapFragment = new MapFragment();
    LoadingFragment loadingFRagment = new LoadingFragment();

    List<Room> dummyRoomList = new ArrayList<>();

    RoomLogic roomLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setNewTheme();

        //roomLogic.removeFilteredRooms();
        //new RequestRooms().execute();

        System.out.println("romstatus launched");
        setContentView(R.layout.activity_main);

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //tableFragment.addRefreshListener(this);
        //mapFragment.addRefreshListener(this);
        tableFragment.addFailureListener(this);
        mapFragment.addFailureListener(this);
        homeFragment.addFailureListener(this);

        new RequestRooms(getApplicationContext()).execute();

    }

    public void navigateTo(int id) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        switch (id) {
            case 0:
                replaceFragment(tableFragment, currentView, AppViews.TABLE);


                break;

            case 1:
                replaceFragment(mapFragment, currentView, AppViews.MAP);


                break;

            case 2:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, filterFragment, AppViews.FILTER.tag)
                        .addToBackStack(null)
                        .commit();
                currentView = AppViews.FILTER;
                break;

            case 3:
                replaceFragment(homeFragment, currentView, AppViews.HOME);
                break;

            case 4:
                replaceFragment(loadingFRagment, currentView, AppViews.LOADING);
        }
    }

    private void replaceFragment(Fragment fragment, AppViews currentView, AppViews newView) {
        //int enterAnimation = R.anim.enter_from_right;
        //int exitAnimation = R.anim.exit_from_right;

        FragmentManager fragmentManager = getSupportFragmentManager();
        /*for (Fragment oldFragment : fragmentManager.getFragments()) {
            if (null != oldFragment) {
                fragmentManager.beginTransaction().remove(oldFragment).commit();
            }
        }*/

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //transaction.setCustomAnimations(enterAnimation, exitAnimation, exitAnimation, enterAnimation);
        //transaction.addToBackStack(null);
        transaction.replace(R.id.content, fragment, newView.tag).commit();

        this.currentView = newView;
    }


    public void goToTable() {
        roomLogic = RoomLogic.getInstance();
        setFilters();
        tableFragment.setRoomList(roomLogic.getFilteredRoomList());
        navigateTo(AppViews.TABLE.id);
    }

    public void goToMap() {
        roomLogic = RoomLogic.getInstance();
        setFilters();
        mapFragment.setRoomList(roomLogic.getFilteredWithNulledRoomList());
        navigateTo(AppViews.MAP.id);
    }

    public void goToFilter() {
        navigateTo(AppViews.FILTER.id);
    }

    public void onFilterClick(View view) {
        navigateTo(AppViews.FILTER.id);
    }


    public enum AppViews {

        TABLE(0, FRAGMENT_TABLE),
        MAP(1, FRAGMENT_MAP),
        FILTER(2, FRAGMENT_FILTER),
        HOME(3, FRAGMENT_HOME),
        LOADING(4, FRAGMENT_LOADING);

        public int id;
        public String tag;

        AppViews(int id, String tag) {
            this.id = id;
            this.tag = tag;
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.navigation_table:
                    goToTable();
                    return true;

                case R.id.navigation_map:
                    goToMap();
                    return true;

                case R.id.navigation_home:
                    navigateTo(AppViews.HOME.id);
                    return true;
            }

            return false;
        }
    };

    private void setFilters() {
        if (AppViews.FILTER == currentView)
            roomLogic.removeFilteredRooms();
    }

    private void setDummyRoomList() {
        dummyRoomList.clear();
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
    }

    private void setNewTheme() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        boolean darkModeIsEnabled = preferences.getBoolean("DarkMode", false);

        int theme = darkModeIsEnabled ? R.style.AppTheme_dark : R.style.AppTheme;
        setTheme(theme);

    }


    private class RequestRooms extends RoomListRequestTask{
        public RequestRooms(Context context) {
            super(context);
        }

        @Override
        public void doOnFailure() {
            showErrorPopup(R.id.navigation);
            setDummyRoomList();
            RoomLogic.getInstance(getApplicationContext(), dummyRoomList);
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.navigation_home);
        }

        @Override
        public void doOnSuccess() {
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.navigation_home);
        }

        @Override
        protected void onPreExecute() {
            navigateTo(AppViews.LOADING.id);
        }
    }


    @Override
    public void notifyFailureListener(int viewId, int currentViewId) {
        if (currentViewId == currentView.id) {
            showErrorPopup(viewId);
        }
        else {
            Snackbar.make(findViewById(R.id.content), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showErrorPopup(int viewId) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            final View popupView = inflater.inflate(R.layout.popup_window_no_connection, (ViewGroup) findViewById(R.id.popupWindow_no_connection));

            // create the popup window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            popupWindow.showAtLocation(findViewById(viewId), Gravity.CENTER, 0, 0);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.content), getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
        }
    }

}
