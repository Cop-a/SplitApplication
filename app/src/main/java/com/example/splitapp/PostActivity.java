package com.example.splitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private ImageView imageLeft;
    private ImageView imageRight;
    private boolean leftRightCheck = false;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageLeft = findViewById(R.id.image_left);
        imageRight = findViewById(R.id.image_right);
        storage  =FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
                imageUri = data.getData();
                imageRight.setImageURI(imageUri);
            }
            else
                {
                    imageUri = data.getData();
                    imageLeft.setImageURI(imageUri);
                }
            uploadpic();
        }
    }

    private void uploadpic() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference mountainsRef = storageReference.child("images/" + randomKey);

        mountainsRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "image Uploaded.", Snackbar.LENGTH_LONG).show();
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