package twitch.angelandroidapps.largefilefinder;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dropbox.core.android.Auth;

import twitch.angelandroidapps.largefilefinder.data.DropboxClientFactory;
import twitch.angelandroidapps.largefilefinder.data.PicassoClient;
import twitch.angelandroidapps.largefilefinder.domain.handlers.PreferenceHandler;

/**
 * Base class for Activities that require auth tokens
 * Will redirect to auth flow if needed
 */
public abstract class DropboxActivity extends AppCompatActivity {

    private static final String TAG = "Angel: Activity";

    @Override
    protected void onResume() {
        super.onResume();

        String accessToken = PreferenceHandler.getAccessToken(this);
        if (accessToken == null) {
            print("retrieving access token from OA2");
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                PreferenceHandler.setAccessToken(this, accessToken);
                initAndLoadData(accessToken);
            }
        } else {
            print("retrieving access token from pref");
            initAndLoadData(accessToken);
        }

        String uid = Auth.getUid();
        String storedUid = PreferenceHandler.getUserId(this);
        if (uid != null && !uid.equals(storedUid)) {
            PreferenceHandler.setUserId(this, uid);
        }
    }

    //DEBUG
    //======
    public void print(String s) {
        Log.d(TAG, s);
    }

    private void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
        PicassoClient.init(getApplicationContext(), DropboxClientFactory.getClient());
        loadData();
    }

    protected abstract void loadData();

    protected boolean hasToken() {
        return PreferenceHandler.getAccessToken(this) != null;
    }
}
