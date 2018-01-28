package com.prestonbui.glancesocial.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.prestonbui.glancesocial.BaseActivity;
import com.prestonbui.glancesocial.R;
import com.prestonbui.glancesocial.adapter.CommentAdapter;
import com.prestonbui.glancesocial.models.Comment;
import com.prestonbui.glancesocial.models.Post;
import com.prestonbui.glancesocial.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by phuongbui on 11/12/17.
 */

public class ViewPostActivity extends BaseActivity {

    private static final String TAG = "ViewPostActivity";

    private Context context;

    //widgets
    private ImageView backButton;

    private ListView listView;

    private CircleImageView rankingImage;
    private TextView handleName;
    private TextView timestamp;
    private TextView rankingName;
    private TextView caption;
    private ImageView postImage;
    private LikeButton upVote, downVote;
    private TextView countUpVote, countDownVote, countComment;
    private EditText commentText;
    private BootstrapButton submitComment;
    private ImageView postOption;

    //vars
    private ArrayList<Post> mPosts;
    private ArrayList<Comment> mComments;
    private CommentAdapter adapter;
    private String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //post details
    private String postID;
    private String userID;
    private String postCaption;
    private String imagePath;
    private long postTime;
    private long numComments;
    private boolean upVotedByCurrentUser, reportedByCurrentUser;

    @Override
    protected void performOnCreate(Bundle state) {

        setContentView(R.layout.fragment_view_post);

        mPosts = new ArrayList<>();
        mComments = new ArrayList<>();

        listView = findViewById(R.id.listView);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup myHeader = (ViewGroup) inflater.inflate(R.layout.activity_view_post, listView, false);
        listView.addHeaderView(myHeader, null, false);

        backButton = findViewById(R.id.image_back_arrow);
        rankingImage = findViewById(R.id.ranking_image);
        handleName = findViewById(R.id.handle_name);
        timestamp = findViewById(R.id.time_stamp);
//        rankingName = findViewById(R.id.ranking_name);
        caption = findViewById(R.id.post_caption);
        postImage = findViewById(R.id.post_image);
        upVote = findViewById(R.id.up_vote_button);
//        downVote = findViewById(R.id.down_vote_button);
        countUpVote = findViewById(R.id.up_vote_count);
//        countDownVote = findViewById(R.id.down_vote_count);
        countComment = findViewById(R.id.comment_count);
        commentText = findViewById(R.id.comment);
        submitComment = findViewById(R.id.submit_comment);
        postOption = findViewById(R.id.post_option);

        postOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(ViewPostActivity.this, v);
                //inflating menu from xml resource
                popup.inflate(R.menu.post_pop_up_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.report_post:
                                getReported(postID);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();

                //close keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);

                //clear text
                commentText.setText("");

            }
        });

        Intent intent = getIntent();
        postID = intent.getStringExtra("post_id");

        getPost(postID);

    }

    private void getCaption() {
        caption.setText(postCaption);
    }

    private void getHandleName() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    handleName.setText(singleSnapshot.getValue(User.class).getHandlename());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTimeDifference() {

        long timeCurrent = System.currentTimeMillis() / 1000;
        long timePost = postTime / 1000;
        long timeDifference = timeCurrent - timePost;

        if (timeDifference < 60) {
            String time = timeDifference + "s";
            timestamp.setText(time);
        } else if (timeDifference < 3600) {
            String time = timeDifference / 60 + "m";
            timestamp.setText(time);
        } else if (timeDifference < 86400) {
            String time = timeDifference / 3600 + "h";
            timestamp.setText(time);
        } else if (timeDifference < 604800) {
            String time = timeDifference / 86400 + "d";
            timestamp.setText(time);
        } else {
            String result = (String) DateUtils.getRelativeTimeSpanString(System.currentTimeMillis(), postTime, 0);
            String time = result.replace("In ", "");
            timestamp.setText(time);
        }
    }

    private void getPostImage() {
        if (imagePath != null) {
            final ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imagePath, postImage);
        }
    }

    private void getPost(String postID) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID);

        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                post.setCaption(dataSnapshot.getValue(Post.class).getCaption());
                post.setDate_created(dataSnapshot.getValue(Post.class).getDate_created());
                post.setImage_path(dataSnapshot.getValue(Post.class).getImage_path());
                post.setUser_id(dataSnapshot.getValue(Post.class).getUser_id());
                post.setComments(dataSnapshot.getValue(Post.class).getComments());

                mPosts.add(post);

                displayPost();

                getComments();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayPost() {

        postCaption = mPosts.get(0).getCaption();
        imagePath = mPosts.get(0).getImage_path();
        userID = mPosts.get(0).getUser_id();
        postTime = mPosts.get(0).getDate_created();
        numComments = mPosts.get(0).getComments();
        postID = mPosts.get(0).getPost_id();

        getUpVote();
        setUpVote();

        getPostImage();
        getHandleName();
        getTimeDifference();
        getCaption();
        getNumComments();
    }


    private void addComment() {

        if (commentText.getText().toString().isEmpty()) {
            Toast.makeText(ViewPostActivity.this, "Please enter your comment", Toast.LENGTH_SHORT).show();
        } else {
            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String commentID = reference.push().getKey();

            Comment comment = new Comment();
            comment.setCaption(commentText.getText().toString());
            comment.setDate_created(System.currentTimeMillis());
            comment.setUser_id(currentUserID);

            reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("comments").child(postID).child(commentID).setValue(comment);
            setNumComment();
            setNumPointCurrentUser();
            setNumPointUser();
            setNumPointPost();
        }
    }

    private void setNumPointPost() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                long numPoints = post.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("points").setValue(numPoints + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNumPointCurrentUser() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(currentUserID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                long numPoints = user.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(currentUserID).child("points").setValue(numPoints + 2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNumPointUser() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                long numPoints = user.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID).child("points").setValue(numPoints + 2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNumComment() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post user = dataSnapshot.getValue(Post.class);
                long numComments = user.getComments();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("comments").setValue(numComments + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNumComments() {

        countComment.setText(String.valueOf(numComments));
    }


    private void getComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("data")
                .child("-Kxzyb5JsUPhsMQAb84X")
                .child("comments")
                .child(postID);

        reference.orderByChild("date_created").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mComments.clear();

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Comment comment = new Comment();

                    comment.setCaption(singleSnapshot.getValue(Comment.class).getCaption());
                    comment.setDate_created(singleSnapshot.getValue(Comment.class).getDate_created());
                    comment.setCaption(singleSnapshot.getValue(Comment.class).getCaption());
                    comment.setUser_id(singleSnapshot.getValue(Comment.class).getUser_id());

                    mComments.add(comment);
                }

                displayComments();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayComments() {

        adapter = new CommentAdapter(ViewPostActivity.this, R.layout.layout_comments, mComments);
        listView.setAdapter(adapter);
        scrollMyListViewToBottom();
        Log.d(TAG, "displayComments: displaying comment");
    }

    private void setUpVote() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        upVote.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("up_votes").child(currentUserID).child(postID).setValue("true");
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("up_votes").child(currentUserID).setValue("true");
                setUserPoints();
                getUpVote();
                setPostPlusPoint(postID);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("up_votes").child(currentUserID).child(postID).removeValue();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("up_votes").removeValue();
                setUserPoints();
                getUpVote();
                setMinusPostPoint(postID);
            }
        });
    }

    private void getReported(final String postID) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("down_votes");
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: check if lalala" + dataSnapshot.hasChild(currentUserID));
                if (dataSnapshot.hasChild(currentUserID)) {
                    reportedByCurrentUser = true;
                } else {
                    reportedByCurrentUser = false;
                }

                setDownVote(postID);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void getUpVote() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("up_votes");
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.hasChild(currentUserID)) {
                    upVotedByCurrentUser = true;
                    setState();
                } else {
                    upVotedByCurrentUser = false;
                }
                long numUpVotes = dataSnapshot.getChildrenCount();

                countUpVote.setText(String.valueOf(numUpVotes));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    private void setUserPoints() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                int points = user.getPoints();
                if (upVotedByCurrentUser) {
                    reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID).child("points").setValue(points + 1);
                } else {
                    reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("users").child(userID).child("points").setValue(points - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setDownVote(final String postID) {
        Log.d(TAG, "setDownVote: reported by current user" + reportedByCurrentUser);
        if(reportedByCurrentUser) {
            Toast.makeText(this, "You reported this post", Toast.LENGTH_SHORT).show();
        } else {
            final DatabaseReference referenceTwo = FirebaseDatabase.getInstance().getReference();
            referenceTwo.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("down_votes").child(currentUserID).child(postID).setValue("true");
            referenceTwo.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("down_votes").child(currentUserID).setValue("true");
            setMinusPostPoint(postID);
        }
    }

    private void setPostPlusPoint(final String postID) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Post post = dataSnapshot.getValue(Post.class);
                long points = post.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("points").setValue(points + 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMinusPostPoint(final String postID) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newReference = reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID);
        newReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Post post = dataSnapshot.getValue(Post.class);
                long points = post.getPoints();
                reference.child("data").child("-Kxzyb5JsUPhsMQAb84X").child("posts").child(postID).child("points").setValue(points - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setState() {
        if (upVotedByCurrentUser) {
            upVote.setLiked(true);
        } else {
            upVote.setLiked(false);
        }

    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

}
