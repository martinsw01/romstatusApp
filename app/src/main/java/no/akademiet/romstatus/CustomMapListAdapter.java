package no.akademiet.romstatus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CustomMapListAdapter extends ArrayAdapter<Room> {
    private Context context;
    private Activity activity;
    private int resource;
    private int parentResource;

    public CustomMapListAdapter(Context context, Activity activity, int resource, int parentResource, List<Room> objects) {
        super(context, resource, objects);
        this.context = context;
        this.activity = activity;
        this.resource = resource;
        this.parentResource = parentResource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Room room = getItem(position);

        String roomNumber = String.valueOf(room.getRoomNumber());

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView roomNumberField = convertView.findViewById(R.id.room_map_number);

        roomNumberField.setText(roomNumber);

        setChildHeightBasedOnParentHeight(roomNumberField);

        boolean filtered = -1 == room.getRoomNumber();

        if (filtered) {
            roomNumberField.setVisibility(View.INVISIBLE);
        }
        else {
            setColorBasedOnStatusAndAirQuality(position, roomNumberField);

            showPopupOnClick(roomNumberField, room);
        }

        return convertView;
    }

    private void setChildHeightBasedOnParentHeight(TextView roomNumberField) {
        ConstraintLayout parent = activity.findViewById(parentResource);

        int parentHeight = parent.getMeasuredHeight();

        ViewGroup.LayoutParams params = roomNumberField.getLayoutParams();
        params.height = parentHeight / (11);
    }

    private void setColorBasedOnStatusAndAirQuality(int position, TextView roomField) {
        Room room = getItem(position);
        Drawable background = roomField.getBackground().mutate();
        if (!RoomLogic.getInstance().isConnected()) {
            roomField.setText("N/A");
            return;
        }
        if (!room.isOccupied()) {
            roomField.setTextColor(context.getResources().getColor(R.color.blackTextColor));

            switch (room.getAirQuality()) {
                case R.string.greatQuality:
                    setBackgroundColor(background, R.color.qualityGreat);
                    break;
                case R.string.mediumQuality:
                    setBackgroundColor(background, R.color.qualityMedium);
                    return;
                case R.string.poorQuality:
                    setBackgroundColor(background, R.color.qualityPoor);
                    break;
                default:
                    setBackgroundColor(background, R.color.qualityGreat);
            }

        }
        else {
            setTextColor(roomField);
        }
    }

    private void setBackgroundColor(Drawable background, int colorResource) {
        int backgroundColor;
        try {
            backgroundColor = context.getResources().getColor(colorResource);
        }
        catch (Exception e) {
            backgroundColor = colorResource;
        }
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(backgroundColor);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(backgroundColor);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(backgroundColor);
        }
    }

    private void showPopupOnClick(final TextView roomField, final Room room) {
        roomField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(roomField, room);
            }
        });
    }

    private void showPopup(View view, Room room) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_window_room, (ViewGroup) activity.findViewById(R.id.popupWindow_room));

        TextView roomNumberField = (TextView) popupView.findViewById(R.id.popup_roomNumber);
        TextView roomNameField = (TextView) popupView.findViewById(R.id.popup_roomName);
        TextView roomStatusField = (TextView) popupView.findViewById(R.id.popup_roomStatus);
        TextView roomAirQualityField = (TextView) popupView.findViewById(R.id.popup_roomAirQuality);

        setBackgroundColor(popupView.getBackground().mutate(), R.color.inactiveColor);

        String roomNumber = String.valueOf(room.getRoomNumber());
        String roomName = room.getRoomName();
        String roomStatus = room.isOccupied() ? context.getString(R.string.occupied) : context.getString(R.string.notOccupied);
        String roomAirQuality = context.getString(room.getAirQuality());

        roomNumberField.setText(roomNumber);
        roomNameField.setText(roomName);
        roomStatusField.setText(roomStatus);
        roomAirQualityField.setText(roomAirQuality);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setElevation(10);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);//location[0], location[1] - 100);
    }

    private void setTextColor(TextView textView) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean darkMode = preferences.getBoolean("DarkMode", false);
        int textColor = darkMode ? R.color.white : R.color.backgroundColorDark;
        textView.setTextColor(context.getResources().getColor(textColor));
    }

}
