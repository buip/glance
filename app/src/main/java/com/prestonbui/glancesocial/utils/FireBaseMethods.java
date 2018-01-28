package com.prestonbui.glancesocial.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prestonbui.glancesocial.MainActivity;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.fragments.FeedsFragment;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.models.School;
import com.prestonbui.glancesocial.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by phuongbui on 10/16/17.
 */

public class FireBaseMethods {

    private static final String TAG = "FireBaseMethods";

    private Context mContext;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private StorageReference mStorageReference;

    //vars
    private double mPhotoUploadProgress = 0;

    //Database

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference myRef;

    private String userID;

    public FireBaseMethods(Context context) {

        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mStorageReference = FirebaseStorage.getInstance().getReference();

        myRef = mFirebaseDatabase.getReference();

        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }

    }

    public boolean checkIfHandleNameAlreadyExists(String handleName, DataSnapshot dataSnapshot) {

        Log.d(TAG, "check if" + handleName + "already exists");

        User user = new User();

        for (DataSnapshot ds : dataSnapshot.child(userID).getChildren()) {
            Log.d(TAG, "checkIfHandleNameAlreadyExists: datasnapshot: " + ds);

            user.setHandlename(ds.getValue(User.class).getHandlename());
            Log.d(TAG, "checkIfHandleNameAlreadyExists: handlename" + user.getHandlename());

            if (user.getHandlename().equals(handleName)) {

                Log.d(TAG, "checkIfHandleNameAlreadyExists: Found A Match" + user.getHandlename());

                return true;
            }
        }
        return false;
    }


    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_posts))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {
            count++;
        }
        return count;
    }

    public void uploadNewPost(final String caption, int count, String imgUrl, Bitmap bm) {
        Log.d(TAG, "uploadNewPost: Attempting to upload new post");

        FilePaths filePaths = new FilePaths();

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

        //converts image to bitmap

        if (bm == null) {

            bm = ImageManager.getBitmap(imgUrl);
            bm = rotateBitMap(imgUrl, bm);

        }

        byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

        UploadTask uploadTask;
        uploadTask = storageReference.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                addPostToDatabase(caption, firebaseUrl.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Post upload failed.");
                Toast.makeText(mContext, "Post upload failed ", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                if(progress - 15 > mPhotoUploadProgress){
                    Toast.makeText(mContext, "Post upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }

                Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
            }
        });

    }

    public Bitmap rotateBitMap(String photoPath, Bitmap bitmap) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "rotateBitMap: caught exception" + ei);
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void addPostToDatabase(String caption, String url) {
        Log.d(TAG, "addPhotoToDatabase: adding post to database.");

        String tags = StringManipulation.getTags(caption);
        String newPostKey = myRef.child("data").child("-Kxzyb5JsUPhsMQAb84X").child(mContext.getString(R.string.dbname_posts)).push().getKey();
        Post post = new Post();
        post.setCaption(caption);
        post.setDate_created(System.currentTimeMillis());
        post.setImage_path(url);
        post.setTags(tags);
        post.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setPost_id(newPostKey);
        setNumPost();
        setNumSchoolPost();

        myRef.child("data").child("-Kxzyb5JsUPhsMQAb84X").child(mContext.getString(R.string.dbname_posts)).child(newPostKey).setValue(post);

    }

    private void setNumPost() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                int numPosts = user.getPosts();
                int numPoints = user.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID).child("posts").setValue(numPosts + 1);
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID).child("points").setValue(numPoints + 3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNumSchoolPost() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("schools").child("-Kxzyb5JsUPhsMQAb84X");
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                School user = dataSnapshot.getValue(School.class);
                long numPoints = user.getPoints();
                reference.child("schools").child("-Kxzyb5JsUPhsMQAb84X").child("points").setValue(numPoints + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Add information to user node
     * Register a new email and password to Firebase Authentication
     *
     * @param handleName
     * @param email
     * @param password
     */
    public void registerNewEmail(final String handleName, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {

                            //send verification email
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();

                            Log.d(TAG, "onComplete: Authstate changed" + userID);

                        }

                    }
                });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(mContext, "Could not send verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void addNewUser(String handleName, String email) {

        User user = new User(handleName, userID, email, 0, 0);

        myRef.child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);
        setNumMember();
    }

    private void setNumMember() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("schools").child("-Kxzyb5JsUPhsMQAb84X");
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                School user = dataSnapshot.getValue(School.class);
                long numMembers = user.getNum_members();
                reference.child("schools").child("-Kxzyb5JsUPhsMQAb84X").child("points").setValue(numMembers + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public User getUserAccountInformation(DataSnapshot dataSnapshot) {

        Log.d(TAG, "getUserAccountInformation: retreiving user account settings from Firebase");

        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            if (ds.getKey().equals(mContext.getString(R.string.db_user))) {

                Log.d(TAG, "getUserAccountInformation: datasnapshot" + ds);

                try {
                    user.setHandlename(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getHandlename());

                    user.setUser_id(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUser_id());

                    user.setEmail(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getEmail());

                    user.setPoints(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getPoints());

                    user.setPosts(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getPosts());

                    Log.d(TAG, "getUserAccountInformation: Retrieve user info" + user.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountInformation: NullPointerException" + e.getMessage());
                }
            }
        }

        return user;

    }


}
