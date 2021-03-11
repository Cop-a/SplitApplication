package com.example.splitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    long unixTime = System.currentTimeMillis() / 1000L;

    private EditText title;
    private ImageView imageLeft;
    private ImageView imageRight;
    private Button uploadButton;
    private boolean leftRightCheck = false;
    public Uri imageUriLeft;
    public Uri imageUriRight;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    public String globalLeftImage;
    public String globalRightImage;
    public String globalUnix;


    private class Post {

        public String uID;
        public String postTitle;
        public String leftURL;
        public String rightURL;
        public String unixTimestamp;

        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Post(String uID, String postTitle, String leftURL, String rightURL, String unixTimestamp) {
            this.uID = uID;
            this.postTitle = postTitle;
            this.leftURL = leftURL;
            this.rightURL = rightURL;
            this.unixTimestamp = unixTimestamp;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title = findViewById(R.id.et_Title);
        imageLeft = findViewById(R.id.image_left);
        imageRight = findViewById(R.id.image_right);
        storage  =FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uploadButton = findViewById(R.id.bt_upload);

        firebaseAuth = FirebaseAuth.getInstance();

        imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightCheck = false;
                choosePic();
            }
        });
        imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightCheck = true;
                choosePic();
            }
        });
    }

    private void choosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            if(leftRightCheck)
            {
                imageUriRight = data.getData();
                imageRight.setImageURI(imageUriRight);
            }
            else
                {
                    imageUriLeft = data.getData();
                    imageLeft.setImageURI(imageUriLeft);
                }
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadpic();
                }
            });
        }
    }

    private void uploadpic() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        globalUnix = String.valueOf(unixTime);
        String tempTitle = title.getText().toString().replaceAll(" ", "_");

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Log.d("left URL", globalLeftImage);
        //Log.d("right URL", globalRightImage);
        Post post = new Post(user.getUid(),tempTitle,globalLeftImage,globalRightImage,globalUnix);

        final String randomKeyLeft = UUID.randomUUID().toString();
        final String randomKeyRight = UUID.randomUUID().toString();
        StorageReference mountainsRef = storageReference.child("UserPosts/" + user.getUid() + "/Posts/" + globalUnix +"-" + tempTitle + "/" + randomKeyLeft);
        //left image
        mountainsRef.putFile(imageUriLeft).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "image Uploaded.", Snackbar.LENGTH_LONG).show();
                storageReference.child("UserPosts/" + user.getUid() + "/Posts/" + globalUnix + "-" + tempTitle + "/" + randomKeyLeft).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String leftImage =task.getResult().toString();
                        post.leftURL = leftImage;
                        mDatabase.child("posts").child(user.getUid()).child(tempTitle).setValue(post);
                        Log.i("left URL", leftImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });
        //right image
        mountainsRef = storageReference.child("UserPosts/" + user.getUid() + "/Posts/" + globalUnix +"-" + tempTitle + "/" + randomKeyRight);
        mountainsRef.putFile(imageUriRight).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "image Uploaded.", Snackbar.LENGTH_LONG).show();
                storageReference.child("UserPosts/" + user.getUid() + "/Posts/" + globalUnix + "-" + tempTitle + "/" + randomKeyRight).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String rightImage =task.getResult().toString();
                        post.rightURL = rightImage;
                        mDatabase.child("posts").child(user.getUid()).child(tempTitle).setValue(post);
                        Log.i("right URL", rightImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int) progressPercent + "%");
            }
        });

    }



}