package com.kailash.gallery.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kailash.gallery.R;
import com.kailash.gallery.adapters.AlbumPickerAdapter;
import com.kailash.gallery.helpers.IOnItemClickListener;
import com.kailash.gallery.helpers.PickerConstants;
import com.kailash.gallery.models.Album;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kailash Chouhan
 * @creationDate 17-Feb-2017
 */
public class ImageAlbumSelectActivity extends HelperActivity implements IOnItemClickListener {

    private ArrayList<Album> albums = new ArrayList<>();
    private TextView errorDisplay;
    private ProgressBar progressBar;
    private AlbumPickerAdapter adapter;
    //    private String screenView = "";
    private ActionBar actionBar;
    private final String[] projection = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    private String TAG = ImageAlbumSelectActivity.class.getSimpleName();

    private int mode;
    private RecyclerView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_select);
        setView(findViewById(R.id.layout_album_select));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);

            actionBar.setDisplayShowTitleEnabled(true);
        }

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_album_select);
        gridView = (RecyclerView) findViewById(R.id.grid_view_album_select);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //gridView.setLayoutManager(new GridLayoutManager(this, 2));

        // Get extras
        PickerConstants.limit = intent.getIntExtra(PickerConstants.INTENT_EXTRA_LIMIT, PickerConstants.DEFAULT_LIMIT);
        mode = intent.getIntExtra(PickerConstants.INTENT_EXTRA_MODE, PickerConstants.MODE_MULTIPLE);

        String folderTitle;
        if (intent.hasExtra(PickerConstants.INTENT_EXTRA_FOLDER_TITLE)) {
            folderTitle = intent.getStringExtra(PickerConstants.INTENT_EXTRA_FOLDER_TITLE);
        } else {
            folderTitle = getString(R.string.title_album_image);
        }

        /** Set activity title */
        if (actionBar != null) {
            actionBar.setTitle(PickerConstants.getFontText(folderTitle));
        }

        adapter = new AlbumPickerAdapter(getApplicationContext(), albums, "image");
        gridView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        orientationBasedUI(getResources().getConfiguration().orientation);

        checkPermission();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }

    private void orientationBasedUI(int orientation) {
        if (adapter != null) {
            adapter.setOrientation(orientation);
        }
        gridView.setLayoutManager(new GridLayoutManager(this, orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(null);
        }
        albums = null;
        if (adapter != null) {
            adapter.releaseResources();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickerConstants.REQUEST_MULTI_SELECT_IMAGE
                && resultCode == RESULT_OK
                && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            default: {
                return false;
            }
        }
    }

    private void loadAlbumsPool() {
        progressBar.setVisibility(View.VISIBLE);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Cursor cursor = getApplicationContext()
                        .getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                projection,
                                null,
                                null,
                                MediaStore.Images.Media.DATE_ADDED);

                if (cursor == null) {
                    return null;
                }
                Log.d(TAG, "call: 1");
                ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
                HashSet<Long> albumSet = new HashSet<>();
                File file;
                if (cursor.moveToLast()) {
                    do {
                        Log.d(TAG, "call: 2");
                        long albumId = cursor.getLong(cursor.getColumnIndex(projection[0]));
                        String album = cursor.getString(cursor.getColumnIndex(projection[1]));
                        String image = cursor.getString(cursor.getColumnIndex(projection[2]));

                        if (!albumSet.contains(albumId)) {
                            // It may happen that some image file paths are still present in cache,
                            // though image file does not exist. These last as long as media
                            // scanner is not run again. To avoid get such image file paths, check
                            // if image file exists.
                            file = new File(image);
                            if (file.exists()) {
                                temp.add(new Album(album, image, photoCountByAlbum(album)));
                                albumSet.add(albumId);
                            }
                        }
                    } while (cursor.moveToPrevious());
                }
                cursor.close();
                updateData(temp);
                return null;
            }
        });
        executorService.shutdown();
    }

    private void updateData(final ArrayList<Album> albumList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (albumList != null) {
                    if (albums == null) {
                        albums = new ArrayList<>();
                    }
                    albums.clear();
                    albums.addAll(albumList);

                    if (adapter != null) {
                        Log.d(TAG, "updateData run: ");
                        progressBar.setVisibility(View.GONE);
                        // gridView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    errorDisplay.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * photo count find based on bucket name(album name)
     *
     * @param bucketName album name
     * @return images count of album
     */
    private int photoCountByAlbum(String bucketName) {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams;
            searchParams = "bucket_display_name = \"" + bucketName + "\"";

            // final String[] columns = { MediaStore.Images.Media.DATA,
            // MediaStore.Images.Media._ID };
            Cursor mPhotoCursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    searchParams, null, orderBy + " DESC");

            if (mPhotoCursor.getCount() > 0) {
                return mPhotoCursor.getCount();
            }
            mPhotoCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    protected void permissionGranted() {
        loadAlbumsPool();
    }

    @Override
    protected void checkPermission() {
        super.checkPermission();
    }

    //
//    @Override
//    protected void hideViews() {
//        progressBar.setVisibility(View.GONE);
////        gridView.setVisibility(View.GONE);
//    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        try {
            Intent intent = new Intent(getApplicationContext(), ImageSelectActivity.class);
            intent.putExtra(PickerConstants.INTENT_EXTRA_ALBUM, albums.get(position).name);
            intent.putExtra(PickerConstants.INTENT_EXTRA_MODE, mode);
            startActivityForResult(intent, PickerConstants.REQUEST_MULTI_SELECT_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
