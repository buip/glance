package com.prestonbui.glancesocial.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.MainActivity;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.fragments.FeedsFragment;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.utils.FireBaseMethods;
import com.prestonbui.glancesocial.utils.Permissions;
import com.prestonbui.glancesocial.utils.StringManipulation;
import com.prestonbui.glancesocial.utils.UniversalImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.prestonbui.glancesocial.utils.ImageManager.getBytesFromBitmap;

/**
 * Created by phuongbui on 10/21/17.
 */

public class ShareActivity extends BaseActivity {

    private static final String TAG = "ShareActivity";

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    //Context
    private Context context = ShareActivity.this;

    //widgets
    private EditText description;
    private ImageView image;
    private ImageView closeButton;
    private ImageView openMedia;
    private ImageView openCamera;
    private ImageView backArrow;
    private BootstrapButton submitPost;
    private ProgressBar progressBar;

    //vars
    private String mAppend = "file:/";
    private String imagePath;
    private boolean isClicked;
    private int imageCount;
    private ArrayList<Image> images = new ArrayList<>();

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference myRef;
    private FireBaseMethods mFireBaseMethods;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share);

        setupFirebaseAuth();

        mFireBaseMethods = new FireBaseMethods(ShareActivity.this);

        //Initialize stuffs
        description = findViewById(R.id.caption);

        image = findViewById(R.id.image_share);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        openMedia = findViewById(R.id.open_media_button);
        openMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImagePicker();
                isClicked = true;
            }
        });

        openCamera = findViewById(R.id.open_camera_button);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraPicker();
                isClicked = true;
            }
        });

        closeButton = findViewById(R.id.close_image_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePath = null;
                closeButton.setVisibility(View.GONE);
                image.setVisibility(View.GONE);
            }
        });

        backArrow = findViewById(R.id.image_back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        submitPost = findViewById(R.id.button_new_post);
        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                //upload the image to Firebase
                Log.d(TAG, "onClick: Attempting to upload new post");
                String caption = description.getText().toString();

                if (imagePath == null) {
                    mFireBaseMethods.addPostToDatabase(caption, null);
                } else {
                    mFireBaseMethods.uploadNewPost(caption, imageCount, imagePath, null);
                }

                Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            setImage();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

    }

    private void startCameraPicker() {

        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#212121")         //  Toolbar color
                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(true)               //  Camera mode
                .setFolderMode(true)                //  Folder mode
                .setSavePath("Glance")         //  Image capture folder name
                .setSelectedImages(images)          //  Selected images
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();
    }

    private void startImagePicker() {

        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
                .setToolbarColor("#212121")         //  Toolbar color
                .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
                .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
                .setProgressBarColor("#4CAF50")     //  ProgressBar color
                .setBackgroundColor("#212121")      //  Background color
                .setCameraOnly(false)               //  Camera mode
                .setMultipleMode(false)              //  Select multiple images or single image
                .setFolderMode(true)                //  Folder mode
                .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
                .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
                .setDoneTitle("Done")               //  Done button title
                .setLimitMessage("You have reached selection limit")    // Selection limit message
                .setMaxSize(10)                     //  Max images can be selected
                .setSavePath("Glance")         //  Image capture folder name
                .setSelectedImages(images)          //  Selected images
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            imagePath = images.get(0).getPath();
            setImage();
        }
        super.onActivityResult(requestCode, resultCode, data);  // THIS METHOD SHOULD BE HERE so that ImagePicker works with fragment
    }

    @Override
    protected void performOnCreate(Bundle state) {

    }


    /**
     * Set Image
     */
    private void setImage() {
        if (isClicked) {
            UniversalImageLoader.setImage(imagePath, image, null, mAppend);
            closeButton.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
        } else {
            closeButton.setVisibility(View.GONE);
        }
    }


    /**
     * verifiy all the permissions passed to the array
     *
     * @param permissions
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     *
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    /*
    ------------------------------------- Firebase ---------------------------------------------------
     */


    /**
     * Set up firebase auth object
     */
    private void setupFirebaseAuth() {

        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");

        mAuth = FirebaseAuth.getInstance();

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFireBaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFireBaseMethods.getImageCount(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
