package no.akademiet.romstatus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class CustomFilterListAdapter extends ArrayAdapter<String[]> {

    private Context context;
    private Activity activity;
    private int resource;
    private int parentHeight;

    public CustomFilterListAdapter(Context context, Activity activity, int resource, String[][] objects) {
        super(context, resource, objects);
        this.context = context;
        this.activity = activity;
        this.resource = resource;
    }

    public void setParentHeight(int parentHeight) {
        this.parentHeight = parentHeight;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String filterText = getItem(position)[0];

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        final TextView filterField = convertView.findViewById(R.id.layout_filter);
        filterField.setText(filterText);
        setFilterActiveListener(filterField, position);

        setHeightBasedOnParentHeight(filterField);

        return convertView;
    }

    private void setFilterActiveListener(final TextView filterField, final int position) {
        final SharedPreferences preferences = activity.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Drawable background = preferences.getBoolean(getItem(position)[1], true) ?
                context.getDrawable(R.drawable.filter_active_background) :
                context.getDrawable(R.drawable.filter_inactive_background);
        filterField.setBackground(background);

        filterField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColorBasedOnFilterActivated(filterField, position, preferences);
            }
        });
    }

    private void setColorBasedOnFilterActivated(TextView filterField, int position, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();

        String activeKeyVal = getItem(position)[1];
        String darkKeyVal = "DarkMode";
        boolean filterIsActive = preferences.getBoolean(activeKeyVal, true);
        boolean darkModeIsEnabled = preferences.getBoolean(darkKeyVal, false);

        if (!filterIsActive) {
            filterField.setBackground(context.getDrawable(R.drawable.filter_active_background));
            filterField.setTextColor(context.getResources().getColor(R.color.backgroundColorDark));
            editor.putBoolean(activeKeyVal, true).apply();
        }
        else {
            filterField.setBackground(context.getDrawable(R.drawable.filter_inactive_background));
            editor.putBoolean(activeKeyVal, false).apply();

            if (darkModeIsEnabled) {
                filterField.setTextColor(context.getResources().getColor(R.color.white));
            }

        }

    }

    private void setHeightBasedOnParentHeight(TextView filterField) {

        ViewGroup.LayoutParams params = filterField.getLayoutParams();
        params.height = parentHeight/getCount();
        filterField.setLayoutParams(params);
    }

    public int getParentHeight(int parentId) {
        ConstraintLayout parent = (ConstraintLayout) activity.findViewById(parentId);
        int parentHeight = parent.getHeight() - getHeaderHeight(parentId);
        int dividerHeight = getListView().getDividerHeight() * (getCount() - 1);
        return (parentHeight - dividerHeight);
    }

    public abstract int getHeaderHeight(int parentId);

    public abstract ListView getListView();
}
