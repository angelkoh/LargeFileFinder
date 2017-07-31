package twitch.angelandroidapps.largefilefinder.domain.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import twitch.angelandroidapps.largefilefinder.R;
import twitch.angelandroidapps.largefilefinder.data.FileThumbnailRequestHandler;
import twitch.angelandroidapps.largefilefinder.domain.handlers.StringHandler;

/**
 * Adapter for file list
 */
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MetadataViewHolder> {
    private List<Metadata> mFiles;
    private final Picasso mPicasso;
    private final Callback mCallback;

    public void setFiles(List<Metadata> files) {
        mFiles = Collections.unmodifiableList(new ArrayList<>(files));
        notifyDataSetChanged();
    }

    public interface Callback {
        void onFolderClicked(FolderMetadata folder);
        void onFileClicked(FileMetadata file);
    }

    public FilesAdapter(Picasso picasso, Callback callback) {
        mPicasso = picasso;
        mCallback = callback;
    }

    @Override
    public MetadataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context)
                //.inflate(R.layout.files_item, viewGroup, false);
                .inflate(R.layout.rv_row_item, viewGroup, false);
        return new MetadataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MetadataViewHolder metadataViewHolder, int i) {
        metadataViewHolder.bind(mFiles.get(i));
    }

    @Override
    public long getItemId(int position) {
        return mFiles.get(position).getPathLower().hashCode();
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    public class MetadataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image)
         ImageView imageView;
        @BindView(R.id.file_name)
         TextView tvFileName;
        @BindView(R.id.file_location)
        TextView tvFileLocation;
        @BindView(R.id.file_size)
        TextView tvFileSize;

        private Metadata mItem;

        public MetadataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mItem instanceof FolderMetadata) {
                mCallback.onFolderClicked((FolderMetadata) mItem);
            }  else if (mItem instanceof FileMetadata) {
                mCallback.onFileClicked((FileMetadata)mItem);
            }
        }

        public void bind(Metadata item) {
            mItem = item;
            tvFileName.setText(mItem.getName());
            tvFileLocation.setText(mItem.getPathDisplay());

            // Load based on file path
            // Prepending a magic scheme to get it to
            // be picked up by DropboxPicassoRequestHandler

            if (item instanceof FileMetadata) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String ext = item.getName().substring(item.getName().indexOf(".") + 1);
                String type = mime.getMimeTypeFromExtension(ext);
                if (type != null && type.startsWith("image/")) {
                    mPicasso.load(FileThumbnailRequestHandler.buildPicassoUri((FileMetadata)item))
                            .placeholder(R.drawable.ic_image_grey_36dp)
                            .error(R.drawable.ic_image_grey_36dp)
                            .into(imageView);
                } else {
                    mPicasso.load(R.drawable.ic_insert_drive_file_blue_36dp)
                            .noFade()
                            .into(imageView);
                }

                String byteCount = StringHandler.humanReadableByteCount(((FileMetadata) item).getSize() ,true);
                tvFileSize.setText(byteCount);

//            } else if (item instanceof FolderMetadata) {
//                mPicasso.load(R.drawable.ic_folder_blue_36dp)
//                        .noFade()
//                        .into(mImageView);
            }
        }
    }
}
