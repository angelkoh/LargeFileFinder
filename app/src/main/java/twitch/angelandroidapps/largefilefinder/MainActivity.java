package twitch.angelandroidapps.largefilefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitch.angelandroidapps.largefilefinder.data.DropboxClientFactory;
import twitch.angelandroidapps.largefilefinder.data.PicassoClient;
import twitch.angelandroidapps.largefilefinder.domain.handlers.PreferenceHandler;
import twitch.angelandroidapps.largefilefinder.domain.tasks.TaskGetCurrentAccount;

public class MainActivity extends DropboxActivity {

    private static final String TAG = "Angel: Main A";
    @BindView(R.id.fab_login)
    FloatingActionButton fabLogin;

    @BindView(R.id.fab_logout)
    FloatingActionButton fabLogout;

    @BindView(R.id.tv_welcome_text)
    TextView tvWelcomeText;

    @BindView(R.id.btn_find_large_files)
    Button btnFindLargeFiles;

    @OnClick(R.id.fab_login)
    void onFabLoginClicked() {
        doLogin();
    }
    @OnClick(R.id.fab_logout)
    void onFabLogoutClicked() {
        doLogout();
    }
    @OnClick(R.id.btn_find_large_files)
    void onBtnFindLargeFilesClicked(){
         startActivity(FilesActivity.getIntent(MainActivity.this, ""));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasToken()) {
            print("Access granted.");
            loadData();
            fabLogin.hide();
            fabLogin.show();
        } else {
            tvWelcomeText.setText("please login");
            fabLogin.show();
            fabLogout.hide();
        }
    }


    @Override
    protected void loadData() {

        new TaskGetCurrentAccount(DropboxClientFactory.getClient(), new TaskGetCurrentAccount.Callback() {
            @Override
            public void onComplete(FullAccount result) {

                StringBuilder sb = new StringBuilder();
                sb.append("\nName: ").append(result.getName().getDisplayName());
                sb.append("\nEmail: ").append(result.getEmail());
                sb.append("\nType: ").append(result.getAccountType().name());
                sb.append("\n");
                tvWelcomeText.setText(sb.toString());

                fabLogin.hide();
                fabLogin.show();


            }

            @Override
            public void onError(Exception e) {
                print("Failed to get Account details. " + e);
            }
        }).execute();
    }

    private void loadFolderData() {

    }


    //OPTION MENU
    //===========
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //AUTHENTICATION
    //==============
    private void doLogin() {
        Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key));


    }

    private void doLogout() {
        PreferenceHandler.setAccessToken(this, null);
        PreferenceHandler.setUserId(this, null);
        tvWelcomeText.setText("logged out.");

        fabLogin.show();
        fabLogout.hide();
    }
}
