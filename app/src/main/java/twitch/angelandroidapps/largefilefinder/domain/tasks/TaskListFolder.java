package twitch.angelandroidapps.largefilefinder.domain.tasks;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;

/**
 * Created by Angel on 28/7/2017.
 */

public class TaskListFolder extends AsyncTask<String, Void, ListFolderResult> {


    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public TaskListFolder(DbxClientV2 dbxClient, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(ListFolderResult result) {
        super.onPostExecute(result);

        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDataLoaded(result);
        }
    }

    @Override
    protected ListFolderResult doInBackground(String... params) {
        try {
            return mDbxClient.files().listFolder(params[0]);
        } catch (DbxException e) {
            mException = e;
        }

        return null;
    }

    public interface Callback {
        void onDataLoaded(ListFolderResult result);

        void onError(Exception e);
    }
}
