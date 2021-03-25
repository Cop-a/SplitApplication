package com.example.splitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "bruih";
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
    public long globalUnix;
    public String tempTitle;
    FirebaseUser user;
    String userID;


    private class Post {

        public String uID;
        public String postTitle;
        public String leftURL;
        public String rightURL;
        public long unixTimestamp;
        public int leftVotes;
        public int rightVotes;


        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Post(String uID, String postTitle, String leftURL, String rightURL, long unixTimestamp, int leftVotes, int rightVotes) {
            this.uID = uID;
            this.postTitle = postTitle;
            this.leftURL = leftURL;
            this.rightURL = rightURL;
            this.unixTimestamp = unixTimestamp;
            this.leftVotes = leftVotes;
            this.rightVotes = rightVotes;

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
        String[] options = {"Gallery", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Pick an input");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if("Gallery".equals(options[which]))
                {
                    Toast.makeText(PostActivity.this, "Gallery selected", Toast.LENGTH_SHORT).show();
                    choosePic(options[which]);
                }
                else if("Camera".equals(options[which]))
                {
                    Toast.makeText(PostActivity.this, "Camera selected", Toast.LENGTH_SHORT).show();
                    choosePic(options[which]);
                }
            }
        });


        imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightCheck = false;
                builder.show();
            }
        });
        imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftRightCheck = true;
                builder.show();
            }
        });
    }

    private void choosePic(String choice) {
        if (choice.equals("Gallery"))
        {
            Intent intent = new Intent();
            intent.setAction(intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        }
        else
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Gallery result code
        if(requestCode == 1 && resultCode == RESULT_OK)
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

                    startActivity(new Intent(PostActivity.this, SecondActivity.class));
                }

            });
        }
        //Camera request code
        if(requestCode == 2 && resultCode == RESULT_OK)
        {
            if(leftRightCheck)
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUriRight = getImageUri(this,photo);
                imageRight.setImageURI(imageUriRight);
            }
            else
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUriLeft = getImageUri(this,photo);
                imageLeft.setImageURI(imageUriLeft);
            }
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadpic();

                    startActivity(new Intent(PostActivity.this, SecondActivity.class));
                }

            });
        }
    }

    private void uploadpic() {


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        //our own unix time because chad
        globalUnix = (1615670565 - unixTime);
        tempTitle = title.getText().toString();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Log.d("left URL", globalLeftImage);
        //Log.d("right URL", globalRightImage);
        Post post = new Post(userID,tempTitle,globalLeftImage,globalRightImage,globalUnix,0 ,0);

        final String randomKeyLeft = UUID.randomUUID().toString();
        final String randomKeyRight = UUID.randomUUID().toString();
        StorageReference mountainsRef = storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix +"-" + tempTitle + "/" + randomKeyLeft);
        //left image
        mountainsRef.putFile(imageUriLeft).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "image Uploaded.", Snackbar.LENGTH_LONG).show();
                storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix + "-" + tempTitle + "/" + randomKeyLeft).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String leftImage =task.getResult().toString();
                        post.leftURL = leftImage;
                        //post left pic to DB
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).setValue(post);
                        Log.i("left URL", leftImage);
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).child("hasVoted").child(userID).setValue(userID);

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
        mountainsRef = storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix +"-" + tempTitle + "/" + randomKeyRight);
        mountainsRef.putFile(imageUriRight).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "image Uploaded.", Snackbar.LENGTH_LONG).show();
                storageReference.child("UserPosts/" + userID + "/Posts/" + globalUnix + "-" + tempTitle + "/" + randomKeyRight).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String rightImage = task.getResult().toString();
                        post.rightURL = rightImage;
                        //post right pic to DB
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).setValue(post);
                        mDatabase.child("posts").child(tempTitle + "-" + globalUnix).child("hasVoted").child(userID).setValue(userID);
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
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



}