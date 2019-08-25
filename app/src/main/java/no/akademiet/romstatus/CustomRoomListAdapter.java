package no.akademiet.romstatus;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CustomRoomListAdapter extends ArrayAdapter<Room> {
    Context context;
    Activity activity;
    int Resource;

    public CustomRoomListAdapter(Context context, Activity activity, int resource, List<Room> objects) {
        super(context, resource, objects);
        this.context = context;
        this.activity = activity;
        Resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String roomNumber = String.valueOf(getItem(position).getRoomNumber());
        String roomName = getItem(position).getRoomName();
        String isOccupied = getItem(position).isOccupied() ?
                context.getString(R.string.occupied) :
                context.getString(R.string.notOccupied);
        String airQuality = context.getString(getItem(position).getAirQuality());

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(Resource, parent, false);

        TextView roomNumberField = convertView.findViewById(R.id.roomNumberHeader);
        TextView roomNameField = convertView.findViewById(R.id.roomNameHeader);
        TextView roomStatusField = convertView.findViewById(R.id.roomStatusHeader);
        TextView roomAirQualityField = convertView.findViewById(R.id.roomAirQualityHeader);

        roomNumberField.setText(roomNumber);
        roomNameField.setText(roomName);
        roomStatusField.setText(isOccupied);
        roomAirQualityField.setText(airQuality);


        return convertView;
    }

    public static void setListViewHeightBasedOnChildren(android.widget.ListView list) {
        ListAdapter adapter = list.getAdapter();

        if (adapter == null) return;

        int desiredWidth = ListView.MeasureSpec.makeMeasureSpec(list.getWidth(), ListView.MeasureSpec.UNSPECIFIED);

        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < adapter.getCount(); ++i) {
            view = adapter.getView(i, view, list);

            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = (totalHeight + (list.getDividerHeight() * (adapter.getCount() - 1))); // TODO: 2019-08-07

        list.setLayoutParams(params);
        list.requestLayout();
    }


}
