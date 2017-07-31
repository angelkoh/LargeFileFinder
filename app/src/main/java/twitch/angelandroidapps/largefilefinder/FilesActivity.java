package twitch.angelandroidapps.largefilefinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import twitch.angelandroidapps.largefilefinder.data.DropboxClientFactory;
import twitch.angelandroidapps.largefilefinder.data.PicassoClient;
import twitch.angelandroidapps.largefilefinder.domain.adapters.FilesAdapter;
import twitch.angelandroidapps.largefilefinder.domain.tasks.TaskGetAllFiles;
import twitch.angelandroidapps.largefilefinder.domain.tasks.TaskListFolder;

/**
 * Created by Angel on 28/7/2017.
 */

public class FilesActivity extends DropboxActivity {

    public final static String EXTRA_PATH = "FilesActivity_Path";
    private static final String TAG = "Angel: Files A";
    private static final int PICKFILE_REQUEST_CODE = 1;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.files_list)
    RecyclerView recyclerView;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.large_file_status)
    TextView largeFileStatus;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String path;
    private FilesAdapter filesAdapter;
    private FileMetadata selectedFile;

    public static Intent getIntent(Context context, String path) {
        Intent filesIntent = new Intent(context, FilesActivity.class);
        filesIntent.putExtra(FilesActivity.EXTRA_PATH, path);
        return filesIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        this.path = path == null ? "" : path;

        setContentView(R.layout.activity_files);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FAB UPLOAD (UNUSED)
        fab.setVisibility(View.GONE);

        //RV
        filesAdapter = new FilesAdapter(PicassoClient.getPicasso(), new FilesAdapter.Callback() {
            @Override
            public void onFolderClicked(FolderMetadata folder) {
                startActivity(FilesActivity.getIntent(FilesActivity.this, folder.getPathLower()));
            }

            @Override
            public void onFileClicked(FileMetadata file) {
                selectedFile = file;
                print("File is clicked:" + file.getName() + ", " + file.getSize());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filesAdapter);

        selectedFile = null;

    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (hasToken()) {
//            loadData();
//        }

    }

    @Override
    protected void loadData() {

        progressBar.setVisibility(View.VISIBLE);
//
//        new TaskListFolder(DropboxClientFactory.getClient(), new TaskListFolder.Callback() {
//            @Override
//            public void onDataLoaded(ListFolderResult result) {
//                progressBar.setVisibility(View.GONE);
//                filesAdapter.setFiles(result.getEntries());
//            }
//
//            @Override
//            public void onError(Exception e) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(FilesActivity.this,
//                        "cannot retrieve folder:" + e,
//                        Toast.LENGTH_SHORT)
//                .show();
//
//            }
//        }).execute(path);


        new TaskGetAllFiles(DropboxClientFactory.getClient(), new TaskGetAllFiles.Callback() {
            @Override
            public void onDataLoaded(List<Metadata> result) {
                progressBar.setVisibility(View.GONE);
                filesAdapter.setFiles(result );
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FilesActivity.this,
                        "cannot retrieve folder:" + e,
                        Toast.LENGTH_SHORT)
                        .show();

            }

            @Override
            public void onProgress(String statusText) {
                if (statusText.contains("LARGE FILE")) {

                    largeFileStatus.setText(statusText);
                }else {
                    status.setText(statusText);
                }
            }
        }).execute(path);

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

}
