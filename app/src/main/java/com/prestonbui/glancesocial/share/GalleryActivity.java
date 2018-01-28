package com.prestonbui.glancesocial.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.utils.FilePaths;
import com.prestonbui.glancesocial.utils.FileSearch;
import com.prestonbui.glancesocial.adapter.GridImageAdapter;

import java.util.ArrayList;

/**
 * Created by phuongbui on 10/21/17.
 */

public class GalleryActivity extends BaseActivity {

    private static final String TAG = "GalleryActivity";

    //constants
    private static final int NUM_GRID_COLUMNS = 3;

    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    public static String mSelectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        galleryImage = findViewById(R.id.galleryImageView);
        gridView = findViewById(R.id.gridView);
        directorySpinner = findViewById(R.id.spinnerDirectory);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        directories = new ArrayList<>();

        ImageView shareClose = findViewById(R.id.image_back_arrow);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        TextView selectImage = findViewById(R.id.text_select_image);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GalleryActivity.this, ShareActivity.class);
                intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                startActivity(intent);

            }
        });

        init();
    }


    @Override
    protected void performOnCreate(Bundle state) {

    }

    private void init() {

        FilePaths filePaths = new FilePaths();

        //check for other folders indide "/storage/emulated/0/pictures"
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            Log.d(TAG, "init: directory: " + directories.get(i));
            int index = directories.get(i).lastIndexOf("/") + 1;
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                try {
                    setupGridView(directories.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            setImage(imgURLs.get(0), galleryImage, mAppend);
            mSelectedImage = imgURLs.get(0);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);

            }
        });

    }

    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
