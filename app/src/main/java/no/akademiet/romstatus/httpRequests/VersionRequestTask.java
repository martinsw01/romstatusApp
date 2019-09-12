package no.akademiet.romstatus.httpRequests;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import org.springframework.http.HttpEntity;

import no.akademiet.romstatus.R;

public abstract class VersionRequestTask extends AsyncTask<Void, Void, Boolean> {

    private String version;
    private Context context;
    View.OnClickListener onUpdateClickListener;

    final private String stringUrl = "http://10.0.0.56:8080";  // TODO: 2019-08-25 set correct url
    private String prefix = "/getLatestVersion/";

    public VersionRequestTask(Context context) {
        version = context.getString(R.string.appVersion);
        prefix = prefix + version;

        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            CustomRestTemplate template = new CustomRestTemplate(1000, true);
            HttpEntity<Boolean> entity = template.getForEntity(stringUrl + prefix, boolean.class);
            template.getForEntity(stringUrl + prefix, Boolean.class);

            return entity.getBody();
        }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean isLatestVersion) {
        super.onPostExecute(isLatestVersion);

        if (null == isLatestVersion) {
            doOnFailure();
            return;
        }

        if (isLatestVersion) {
            doIfTrue();
        }
        else {
            doIfFalse();
        }
    }


    public abstract void doOnFailure();

    public abstract void doIfTrue();

    private void doIfFalse() {
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(stringUrl));
        context.startActivity(browserIntent);
    }
}
