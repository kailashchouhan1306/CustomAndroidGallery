package com.example.gallerydemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kailash.gallery.helpers.PickerConstants;
import com.kailash.gallery.models.MediaFile;
import com.kailash.gallery.picker.GalleryPicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<MediaFile> images = data.getParcelableArrayListExtra(PickerConstants.INTENT_SELECTED_FILES);
            StringBuilder sb = new StringBuilder();
            if (requestCode == PickerConstants.REQUEST_MULTI_SELECT_IMAGE) {
                for (int i = 0; i < images.size(); i++) {
                    sb.append(images.get(i).path + "\n");
                }
                Toast.makeText(MainActivity.this, "Image Select", Toast.LENGTH_SHORT).show();
            }

            if (requestCode == PickerConstants.REQUEST_MULTI_SELECT_VIDEO) {
                for (int i = 0; i < images.size(); i++) {
                    sb.append(images.get(i).path + "\n");
                }
                Toast.makeText(MainActivity.this, "Video Select", Toast.LENGTH_SHORT).show();
            }
            mTextMessage.setText(sb.toString());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                Typeface font = Typeface.createFromAsset(getAssets(), "fonts/montserrat_light.otf");
                GalleryPicker.create(MainActivity.this)
                        .folderTitle("Folder")
                        .imageTitle("Tap image to select")
                        .single() // default multi select
                        .type(PickerConstants.TYPE_IMAGE) // default type is image
                        .setCustomFont(font) // set custom font
                        .start(PickerConstants.REQUEST_MULTI_SELECT_IMAGE);
                break;

            case R.id.button2:
                GalleryPicker.create(MainActivity.this)
                        .folderTitle("Folder")
                        .imageTitle("Tap image to select")
                        .multi() // default multi select
                        .limit(10)// default limit is 10
                        .type(PickerConstants.TYPE_IMAGE) // default type is image
                        .start(PickerConstants.REQUEST_MULTI_SELECT_IMAGE);
                break;

            case R.id.button3:
                GalleryPicker.create(MainActivity.this)
                        .folderTitle("Folder")
                        .imageTitle("Tap Video to select")
                        .single() // default multi select
                        .type(PickerConstants.TYPE_VIDEO) // default type is image
                        .start(PickerConstants.REQUEST_MULTI_SELECT_VIDEO);
                break;

            case R.id.button4:
                GalleryPicker.create(MainActivity.this)
                        .folderTitle("Folder")
                        .imageTitle("Tap Video to select")
                        .multi() // default multi select
                        .limit(10)// default limit is 10
                        .type(PickerConstants.TYPE_VIDEO) // default type is image
                        .start(PickerConstants.REQUEST_MULTI_SELECT_VIDEO);
                break;

            case R.id.button5:
                GalleryPicker.create(MainActivity.this)
                        .folderTitle("Folder")
                        .imageTitle("Tap Video to select")
                        .multi() // default multi select
                        .limit(10)// default limit is 10
                        .videoMaxDuration(30000) // 30 seconds (in milliseconds)
                        .type(PickerConstants.TYPE_VIDEO) // default type is image
                        .start(PickerConstants.REQUEST_MULTI_SELECT_VIDEO);
                break;

            default:
                break;
        }
    }
}
