package twitch.angelandroidapps.largefilefinder.domain.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import twitch.angelandroidapps.largefilefinder.domain.handlers.StringHandler;

/**
 * Created by Angel on 28/7/2017.
 */

public class TaskGetAllFiles extends AsyncTask<String, String, List<Metadata>> {

    private static final String TAG = "Angel: GetFiles AT";
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public TaskGetAllFiles(DbxClientV2 mDbxClient, Callback mCallback) {
        this.mDbxClient = mDbxClient;
        this.mCallback = mCallback;
    }

    @Override
    protected List<Metadata> doInBackground(String... params) {

        print(">starting query");
        List<Metadata> results = new ArrayList<>();
        Set<String> processedFolder = new HashSet<>();
        appendData(params[0], processedFolder, results);

        return results;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mCallback.onProgress(values[0]);
    }

    @Override
    protected void onPostExecute(List<Metadata> result) {
        super.onPostExecute(result);

        if (result.isEmpty() && mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDataLoaded(result);
        }
    }

    private void appendData(String path, Set<String> processedFolder, List<Metadata> results) {

        List<Metadata> entries = null;
//        try {
//
//            entries = mDbxClient.files().listFolder(path).getEntries();
//
//        } catch (DbxException e) {
//            print("LARGE FILE Has Error: "+e);
//            publishProgress("LARGE FILE has Error: "+e);
//            mException = e;
//        }
//        if (entries != null) {
//
//            print("loading: '" + path + "'");
//            print("    Found: " + entries.size() + " items");
//            publishProgress("Path: " + path + "(" + entries.size() + ", " + results.size() + " total files)");
//
//            for (Metadata data : entries) {
//                if (data instanceof FileMetadata) {
//                    results.add(data);
//                    FileMetadata file = (FileMetadata) data;
//                    if (file.getSize() > 10_000_000) {
//                        print("\n\nfound big file: " + file.getName()
//                                + " at " + file.getPathLower() + " " + file.getSize() + "b\n\n");
//
//                        publishProgress("LARGE FILE: " + file.getName() + "(" + file.getSize() + ")" + file.getPathLower());
//                    }
//
//                } else if (data instanceof FolderMetadata) {
//                    FolderMetadata fmd = (FolderMetadata) data;
//                    path = fmd.getPathLower();
//                    appendData(path, processedFolder, results);
//                }
//            }
//            print("  list size: " + results.size());


        ListFolderResult listFolder = null; //= mDbxClient.files().listFolder(path);
        try {
            listFolder = mDbxClient.files().listFolderBuilder(path)
                    .withRecursive(true)
                    .withIncludeDeleted(false)
                    .withIncludeHasExplicitSharedMembers(true)
                    .withIncludeMediaInfo(true)
                    .start();

        } catch (DbxException e) {
            print("LARGE FILE Has Error: " + e);
            publishProgress("LARGE FILE has Error: " + e);
            entries = null;
            mException = e;
        }

        int filesProcessed = 0;
        while (listFolder != null) {
            entries = listFolder.getEntries();

            if (entries != null) {


                String cursor = listFolder.getCursor();
                print("entries: " + entries.size() + ", " + listFolder.getHasMore() + ",cursor: " + cursor);

                for (Metadata data : entries) {
                    if (data instanceof FileMetadata) {
                        filesProcessed++;
                        FileMetadata file = (FileMetadata) data;
                        if (file.getSize() > 10_000_000) {
                            results.add(data);
                            String byteCount = StringHandler.humanReadableByteCount(file.getSize(), true);
                            print("\n\nfound big file: " + file.getName()
                                    + " at " + file.getPathLower() + " " + byteCount + "\n\n");

                            publishProgress("LARGE FILE: " + file.getName() + "(" + byteCount + ")" + file.getPathLower());
                        }

                    } else if (data instanceof FolderMetadata) {
                        FolderMetadata fmd = (FolderMetadata) data;
                        print("Folder: " + fmd.getName() + "," + fmd.getPathDisplay());
                        publishProgress("Folder: " + fmd.getName() + "," + fmd.getPathDisplay());
                    }
                }

                if (listFolder.getHasMore()) {
                    try {
                        print("next loop, files processed: " + filesProcessed);
                        listFolder = mDbxClient.files().listFolderContinue(cursor);
                    } catch (DbxException e) {

                        print("LARGE FILE Has Error (continue): " + e);
                        publishProgress("LARGE FILE has Error: " + e);
                        mException = e;
                        break;
                    }
                } else {
                    break;
                }
            }//entries null?
        }//loop till break encountered

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
