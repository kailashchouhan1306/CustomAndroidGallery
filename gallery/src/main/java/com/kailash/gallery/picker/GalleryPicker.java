package com.kailash.gallery.picker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;

import com.kailash.gallery.R;
import com.kailash.gallery.activity.ImageAlbumSelectActivity;
import com.kailash.gallery.activity.VideoAlbumSelectActivity;
import com.kailash.gallery.helpers.PickerConstants;

/**
 * @author Kailash Chouhan
 * @creationDate 28-Mar-17
 */

public abstract class GalleryPicker {
    private int mode;
    private int limit;
    private String imageTitle;
    private String folderTitle;
    private String type;
    private int videoMaxDuration;

    public abstract void start(int requestCode);

    public static class GalleryPickerWithActivity extends GalleryPicker {

        private Activity activity;

        public GalleryPickerWithActivity(Activity activity) {
            this.activity = activity;
            init(activity);
        }

        @Override
        public void start(int requestCode) {
            Intent intent = getIntent(activity);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static class GalleryPickerWithFragment extends GalleryPicker {

        private Fragment fragment;

        public GalleryPickerWithFragment(Fragment fragment) {
            this.fragment = fragment;
            init(fragment.getActivity());
        }

        @Override
        public void start(int requestCode) {
            Intent intent = getIntent(fragment.getActivity());
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    public void init(Activity activity) {
        this.mode = PickerConstants.MODE_MULTIPLE;
        this.limit = PickerConstants.DEFAULT_LIMIT;
        this.folderTitle = activity.getString(R.string.title_album_image);
        this.imageTitle = activity.getString(R.string.title_select_image);
        this.type = PickerConstants.TYPE_IMAGE;
        this.videoMaxDuration = PickerConstants.VIDEO_MAX_DURATION;
        PickerConstants.CUSTOM_TYPEFACE = Typeface.DEFAULT;
    }

    public static GalleryPickerWithActivity create(Activity activity) {
        return new GalleryPickerWithActivity(activity);
    }

    public static GalleryPickerWithFragment create(Fragment fragment) {
        return new GalleryPickerWithFragment(fragment);
    }

    /**
     * set single select image/video
     */
    public GalleryPicker single() {
        mode = PickerConstants.MODE_SINGLE;
        return this;
    }

    /**
     * set multi select image/video
     */
    public GalleryPicker multi() {
        mode = PickerConstants.MODE_MULTIPLE;
        return this;
    }

    /**
     * set max selected limit count
     *
     * @param count max selected limit count
     */
    public GalleryPicker limit(int count) {
        this.limit = count;
        return this;
    }

    /**
     * set Image/Video album title
     *
     * @param title title for Image/Video album
     */
    public GalleryPicker folderTitle(String title) {
        this.folderTitle = title;
        return this;
    }

    /**
     * set title for image/video select view
     *
     * @param title title for image/video select
     */
    public GalleryPicker imageTitle(String title) {
        this.imageTitle = title;
        return this;
    }

    /**
     * set type between Image/Video
     *
     * @param type PickerConstants.TYPE_IMAGE/PickerConstants.TYPE_VIDEO
     */
    public GalleryPicker type(String type) {
        this.type = type;
        return this;
    }

    /**
     * video duration in milliseconds
     */
    public GalleryPicker videoMaxDuration(int duration) {
        this.videoMaxDuration = duration + 999;
        return this;
    }

    /**
     * set custom typeface for text
     */
    public GalleryPicker setCustomFont(Typeface typeface) {
        PickerConstants.CUSTOM_TYPEFACE = typeface;
        return this;
    }

    public Intent getIntent(Activity activity) {
        Intent intent;
        if (type.equalsIgnoreCase(PickerConstants.TYPE_VIDEO)) {
            intent = new Intent(activity, VideoAlbumSelectActivity.class);
            intent.putExtra(PickerConstants.INTENT_VIDEO_MAX_DURATION, videoMaxDuration);
        } else {
            intent = new Intent(activity, ImageAlbumSelectActivity.class);
        }
        intent.putExtra(PickerConstants.INTENT_EXTRA_MODE, mode);
        intent.putExtra(PickerConstants.INTENT_EXTRA_LIMIT, limit);
        intent.putExtra(PickerConstants.INTENT_EXTRA_FOLDER_TITLE, folderTitle);
        intent.putExtra(PickerConstants.INTENT_EXTRA_IMAGE_TITLE, imageTitle);
        return intent;
    }
}
