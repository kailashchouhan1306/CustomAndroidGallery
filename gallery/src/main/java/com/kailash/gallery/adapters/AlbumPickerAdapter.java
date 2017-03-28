package com.kailash.gallery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kailash.gallery.R;
import com.kailash.gallery.helpers.IOnItemClickListener;
import com.kailash.gallery.helpers.PickerConstants;
import com.kailash.gallery.models.Album;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Kailash Chouhan
 * @creationDate 17-Feb-2017
 */
public class AlbumPickerAdapter extends RecyclerView.Adapter<AlbumPickerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Album> albumsList;
    private IOnItemClickListener onItemClickListener;
    private String cameFrom;
    private String TAG = AlbumPickerAdapter.class.getSimpleName();

    public AlbumPickerAdapter(Context context, ArrayList<Album> albums, String cameFrom) {
        this.context = context;
        this.albumsList = albums;
        this.cameFrom = cameFrom;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_view_item_album_select, parent, false);
        return new AlbumPickerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Album album = albumsList.get(holder.getAdapterPosition());
        int size = PickerConstants.getViewWidthHeight(context);

        holder.itemView.getLayoutParams().width = size;
        holder.itemView.getLayoutParams().height = size;

        if (cameFrom.equalsIgnoreCase("video")) {
            holder.video_icon.setVisibility(View.VISIBLE);
        } else {
            holder.video_icon.setVisibility(View.GONE);
        }

        String textToShow = album.name;
        if (album.count != 0) {
            textToShow = textToShow + " (" + album.count + ")";
        }

        holder.textView.setText(PickerConstants.getFontText(textToShow));
        Log.d(TAG, "file uri: " + album.cover);
        Glide.with(context)
                .load(new File(album.cover))
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return albumsList == null ? 0 : albumsList.size();
    }

    public void setOnItemClickListener(IOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void releaseResources() {
        albumsList = null;
        context = null;
    }
//
//    public void setLayoutParams(int size) {
//        this.size = size;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private ImageView video_icon;

        private ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_view_album_image);
            textView = (TextView) view.findViewById(R.id.text_view_album_name);
            video_icon = (ImageView) view.findViewById(R.id.video_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onRecyclerItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }
}
