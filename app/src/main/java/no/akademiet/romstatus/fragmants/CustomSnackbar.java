package no.akademiet.romstatus.fragmants;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import no.akademiet.romstatus.R;

public class CustomSnackbar {

    Snackbar snackbar;

    private CustomSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }

    public static Snackbar make(Context context, Activity activity) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.content), context.getString(R.string.successfull), Snackbar.LENGTH_SHORT);

        View snackbarView = (View) snackbar.getView();
        TextView snackbarTextField = (TextView) snackbarView.findViewById(R.id.snackbar_text);

        snackbarView.setBackground(context.getDrawable(R.drawable.room_background));
        snackbarTextField.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        ViewGroup.LayoutParams params = snackbarView.getLayoutParams();
        params.height = 150;
        snackbarView.setLayoutParams(params);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            snackbarTextField.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            snackbarTextField.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        return snackbar;
    }

}
