package com.kailash.gallery.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kailash.gallery.R;
import com.kailash.gallery.helpers.IOnItemClickListener;
import com.kailash.gallery.helpers.PickerConstants;
import com.kailash.gallery.models.MediaFile;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Kailash Chouhan
 * @creationDate 17-Feb-2017
 */
public class MediaPickerAdapter extends RecyclerView.Adapter<MediaPickerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MediaFile> arrayList;
    private IOnItemClickListener onItemClickListener;
    private String TAG = MediaPickerAdapter.class.getSimpleName();

    public MediaPickerAdapter(Context context, ArrayList<MediaFile> images) {
        this.context = context;
        this.arrayList = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_view_item_image_select, parent, false);
        return new MediaPickerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        MediaFile mediaFile = arrayList.get(viewHolder.getAdapterPosition());

        int size = PickerConstants.getViewWidthHeight(context);

        viewHolder.imageView.getLayoutParams().width = size;
        viewHolder.imageView.getLayoutParams().height = size;

        viewHolder.itemView.getLayoutParams().width = size;
        viewHolder.itemView.getLayoutParams().height = size;

        if (mediaFile.isSelected) {
            viewHolder.viewAlpha.setAlpha(0.5f);
            viewHolder.frameLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.ic_done_white));
        } else {
            viewHolder.viewAlpha.setAlpha(0.0f);
            viewHolder.frameLayout.setForeground(null);
        }
        Log.d(TAG, "file uri: " + mediaFile.path);

        Glide.with(context)
                .load(new File(mediaFile.path))
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .into(viewHolder.imageView);

        viewHolder.tv_size.setText(PickerConstants.getFontText(PickerConstants.getBytesString(mediaFile.imageSize)));

        if (mediaFile.type.equals("video")) {
            viewHolder.tvDuration.setVisibility(View.VISIBLE);
            viewHolder.tvDuration.setText(PickerConstants.getFontText(PickerConstants.getDurationMark(mediaFile.videoDuration)));
        } else {
            viewHolder.tvDuration.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public void setOnItemClickListener(IOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void releaseResources() {
        arrayList = null;
        context = null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private View viewAlpha;
        private TextView tvDuration, tv_size;
        private FrameLayout frameLayout;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.image_view_image_select);
            this.viewAlpha = view.findViewById(R.id.view_alpha);
            this.tvDuration = (TextView) view.findViewById(R.id.tvDuration);
            this.frameLayout = (FrameLayout) view.findViewById(R.id.frameLayout);
            this.tv_size = (TextView) view.findViewById(R.id.tv_size);
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
