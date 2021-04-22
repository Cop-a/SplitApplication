package com.example.splitapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileSettingFragment extends Fragment {

    private static final String TAG = "yp";
    /* private EditText Name;
        private EditText Username;
        private EditText Phone;
        private EditText Biography;
        private EditText Email;
    */  private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Button saveB;
    public Uri imageUri;
    public CircleImageView profilePic;
    public CircleImageView nav_pfp;
    public ImageView back_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        firebaseAuth = FirebaseAuth.getInstance();
        storage  = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        profilePic = view.findViewById(R.id.msg_profile_img);
        EditText Name = (EditText) view.findViewById(R.id.et_profile_name);
        EditText Username = (EditText) view.findViewById(R.id.et_profile_username);
        EditText Phone = (EditText) view.findViewById(R.id.et_profile_phone);
        EditText Email = (EditText) view.findViewById(R.id.et_profile_email);
        Button saveB   = (Button)view.findViewById(R.id.btn_profile_apply);
        nav_pfp = view.findViewById(R.id.img_profile_pfp);
        back_btn = view.findViewById(R.id.iv_prosetting_back_btn);

        String pro_u = user.getUid();
        String pro_e = user.getEmail();

        Email.setText(pro_e);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).child("profileUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else
                    {
                        Log.d("HERE", mDatabase.child("users").child(user.getUid()).child("profileUrl").toString());
                        Glide.with(profilePic.getContext()).load(task.getResult().getValue()).into(profilePic);
                    }
            }
        });

        mDatabase.child("users").child(user.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Username.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

            }
        });

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child("users").child(user.getUid()).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            StorageReference mountainsRef = storageReference.child("UserProfiles/" + user.getUid() + "/" + "profilePic");
                            //left image
                            if (imageUri != null){
                            mountainsRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference.child("UserProfiles/" + user.getUid() + "/" + "profilePic").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String profileImage =task.getResult().toString();
                                            //post left pic to DB
                                            mDatabase.child("users").child(user.getUid()).child("profileUrl").setValue(profileImage);

                                            Log.i("left URL", profileImage);
                                        }
                                    });
                                }
                            });



                            }

                            mDatabase.child("users").child(user.getUid()).child("username").setValue(Username.getText().toString());

                        }
                    }
                });



            }

        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
        }
    }
}
