package com.kailash.gallery.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kailash.gallery.R;
import com.kailash.gallery.adapters.MediaPickerAdapter;
import com.kailash.gallery.helpers.IOnItemClickListener;
import com.kailash.gallery.helpers.PickerConstants;
import com.kailash.gallery.models.MediaFile;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kailash Chouhan
 * @creationDate 17-Feb-2017
 */
public class ImageSelectActivity extends AppCompatActivity implements IOnItemClickListener {

    private ArrayList<MediaFile> images = new ArrayList<>();
    private String album;
    private TextView errorDisplay;
    private ProgressBar progressBar;
    private ActionBar actionBar;
    private ActionMode actionMode;
    private int countSelected;
    private MediaPickerAdapter adapter;
    private final String[] projection = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA};
    private String TAG = ImageSelectActivity.class.getSimpleName();

    private int mode;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
//        setView(findViewById(R.id.layout_image_select));

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
        album = intent.getStringExtra(PickerConstants.INTENT_EXTRA_ALBUM);

        errorDisplay = (TextView) findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_image_select);
        recyclerView = (RecyclerView) findViewById(R.id.grid_view_image_select);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
//        recyclerView.setLayoutManager(gridLayoutManager);

        /** Get extras */
        mode = intent.getIntExtra(PickerConstants.INTENT_EXTRA_MODE, PickerConstants.MODE_MULTIPLE);

        String imageTitle;
        if (intent.hasExtra(PickerConstants.INTENT_EXTRA_IMAGE_TITLE)) {
            imageTitle = intent.getStringExtra(PickerConstants.INTENT_EXTRA_IMAGE_TITLE);
        } else {
            imageTitle = getString(R.string.title_select_image);
        }

        /** Set activity title */
        if (actionBar != null) {
            actionBar.setTitle(PickerConstants.getFontText(imageTitle));
        }

        /** Init folder and image adapter */
        adapter = new MediaPickerAdapter(getApplicationContext(), images);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        orientationBasedUI(getResources().getConfiguration().orientation);
        loadImagesPool();
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(null);
        }
        images = null;
        if (adapter != null) {
            adapter.releaseResources();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

//    @Override
//    protected void permissionGranted() {
//        loadImagesPool();
//    }
//
//    @Override
//    protected void hideViews() {
//        progressBar.setVisibility(View.GONE);
////        recyclerView.setVisibility(View.GONE);
//    }

    private void loadImagesPool() {
        progressBar.setVisibility(View.VISIBLE);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
//                File file;
//                HashSet<Long> selectedImages = new HashSet<>();
//                if (images != null) {
//                    MediaFile image;
//                    for (int i = 0, l = images.size(); i < l; i++) {
//                        image = images.get(i);
//                        file = new File(image.path);
//                        Log.d(TAG, "loadImagesPool: " + file.length());
//                        if (file.exists() && image.isSelected) {
//                            selectedImages.add(image.id);
//                        }
//                    }
//                }

                Cursor cursor = getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                projection,
                                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?",
                                new String[]{album},
                                MediaStore.Images.Media.DATE_ADDED);

                if (cursor == null) {
                    //sendMessage(PickerConstants.ERROR);
                    return null;
                }
                ArrayList<MediaFile> temp = new ArrayList<>(cursor.getCount());
                if (cursor.moveToLast()) {
                    do {
                        long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                        String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                        String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                        //boolean isSelected = selectedImages.contains(id);

                        File file = new File(path);
                        if (file.exists()) {
                            Log.d(TAG, "File Length: " + file.length());
                            long length = file.length();
                            temp.add(new MediaFile(id, name, path, false, "image", length));
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

    private void updateData(final ArrayList<MediaFile> mediaFilesList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaFilesList != null) {
                    if (images == null) {
                        images = new ArrayList<>();
                    }
                    images.clear();
                    images.addAll(mediaFilesList);

                    // If adapter is null, this implies that the loaded images will be shown
                    // for the first time, hence send FETCH_COMPLETED message.
                    // However, if adapter has been initialised, this thread was run either
                    // due to the activity being restarted or content being changed.
                    if (adapter != null) {
                        progressBar.setVisibility(View.GONE);
                        // recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    errorDisplay.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        if (actionMode == null) {
            actionMode = ImageSelectActivity.this.startActionMode(callback);
        }
        toggleSelection(position);
        String actionTitle = countSelected + " " + getString(R.string.selected);
        actionMode.setTitle(PickerConstants.getFontText(actionTitle));

        if (countSelected == 0) {
            actionMode.finish();
        }
    }

    ArrayList<MediaFile> selectedImages = new ArrayList<>();

    private void toggleSelection(int position) {
        if (!images.get(position).isSelected && countSelected >= PickerConstants.limit) {
            Toast.makeText(
                    getApplicationContext(),
                    PickerConstants.getFontText(String.format(getString(R.string.image_limit_exceeded), PickerConstants.limit)),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (new File(images.get(position).path).length() == 0) {
            Toast.makeText(getApplicationContext(),
                    PickerConstants.getFontText(getString(R.string.cant_select_image)),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        images.get(position).isSelected = !images.get(position).isSelected;
        if (images.get(position).isSelected) {
            selectedImages.add(images.get(position));
            countSelected++;
        } else {
            selectedImages.remove(images.get(position));
            countSelected--;
        }
        adapter.notifyItemChanged(position);

        if (mode == PickerConstants.MODE_SINGLE) {
            sendIntent();
        }
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.menu_contextual_action_bar, menu);

            // get menu item
            MenuItem mi = menu.getItem(0);
            // set font to menu item
            mi.setTitle(PickerConstants.getFontText(mi.getTitle().toString()));

            actionMode = mode;
            countSelected = 0;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.menu_item_add_image) {
                sendIntent();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (countSelected > 0) {
                deselectAll();
            }
            actionMode = null;
        }
    };

    private void deselectAll() {
        for (int i = 0, l = images.size(); i < l; i++) {
            images.get(i).isSelected = false;
        }
        countSelected = 0;
        adapter.notifyDataSetChanged();
    }

//    private ArrayList<MediaFile> getSelected() {
//        ArrayList<MediaFile> selectedImages = new ArrayList<>();
//        for (int i = 0, l = images.size(); i < l; i++) {
//            if (images.get(i).isSelected) {
//                selectedImages.add(images.get(i));
//            }
//        }
//        return selectedImages;
//    }

    private void sendIntent() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConstants.INTENT_SELECTED_FILES, selectedImages);//getSelected());
        setResult(RESULT_OK, intent);
        finish();
    }
}
