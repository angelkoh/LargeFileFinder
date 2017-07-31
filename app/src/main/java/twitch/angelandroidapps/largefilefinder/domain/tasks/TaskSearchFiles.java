package twitch.angelandroidapps.largefilefinder.domain.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.SearchResult;

import java.util.List;

/**
 * Created by Angel on 31/7/2017.
 */

public class TaskSearchFiles extends AsyncTask<String, String, SearchResult> {

    private static final String TAG = "Angel: SearchFiles AT";
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public TaskSearchFiles(DbxClientV2 mDbxClient, Callback mCallback) {
        this.mDbxClient = mDbxClient;
        this.mCallback = mCallback;
    }

    @Override
    protected SearchResult doInBackground(String... strings) {
        return null;
    }




    private void print(String s) {
        Log.d(TAG, s);
    }

    public interface Callback {
        void onDataLoaded(List<Metadata> result);

        void onError(Exception e);

        void onProgress(String status);
    }

}
