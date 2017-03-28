package com.kailash.gallery.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author Kailash Chouhan
 * @creationDate 17-Feb-2017
 */
public class PickerConstants {
    public static final int PERMISSION_REQUEST_CODE = 1000;
    public static final int VIDEO_MAX_DURATION = 0;

    /**
     * Request code for permission has to be < (1 << 8)
     * Otherwise throws java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
     */
    public static final int REQUEST_MULTI_SELECT_IMAGE = 2000;
    public static final int REQUEST_MULTI_SELECT_VIDEO = 3000;

    public static final String INTENT_EXTRA_ALBUM = "album";
    public static final String INTENT_SELECTED_FILES = "selectedMedia";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final int DEFAULT_LIMIT = 10;
    public static final String INTENT_EXTRA_MODE = "mode";
    public static final String INTENT_EXTRA_FOLDER_TITLE = "folderTitle";
    public static final String INTENT_EXTRA_IMAGE_TITLE = "imageTitle";
    public static final String INTENT_VIDEO_MAX_DURATION = "videoMaxDuration";

    public static final int MODE_SINGLE = 1;
    public static final int MODE_MULTIPLE = 2;

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";

    public static Typeface CUSTOM_TYPEFACE;

    //Maximum number of images that can be selected at a time
    public static int limit;

    public static String getDurationMark(int time) {
        /*
         * fix for the gallery picker crash
         * if it couldn't extractMetadata() of a media file
         * time was null
         */
        time = time == 0 ? 0 : time;
        //bam crash - no more :)
        int timeInMillis = time;
        int duration = timeInMillis / 1000;
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(":");
        }
        if (minutes < 10) {
            sb.append("0").append(minutes);
        } else {
            sb.append(minutes);
        }
        sb.append(":");
        if (seconds < 10) {
            sb.append("0").append(seconds);
        } else {
            sb.append(seconds);
        }
        return sb.toString();
    }

    public static int getViewWidthHeight(Context context) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * Method used to create Fonts Spannable String
     *
     * @param errMsg string message
     * @return spannable string with fonts
     */
    public static SpannableStringBuilder getFontText(String errMsg) {
        SpannableStringBuilder ssbuilder = null;
        try {
            if (!isNullOrEmpty(errMsg)) {
                ssbuilder = new SpannableStringBuilder(errMsg);
                if (CUSTOM_TYPEFACE == Typeface.DEFAULT) {
                    return ssbuilder;
                }
                ssbuilder.setSpan(new CustomTypefaceSpan("", CUSTOM_TYPEFACE), 0, ssbuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return ssbuilder;
    }

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    /**
     * method used to check string is null or empty
     *
     * @param s string to be checked
     * @return true if string is null or empty else false
     */
    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.length() == 0) || (s.equals("")) || (s.equalsIgnoreCase("null"));
    }
}
