package com.example.splitapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class ProfileFragment extends Fragment {

   /* private EditText Name;
    private EditText Username;
    private EditText Phone;
    private EditText Biography;
    private EditText Email;
*/    private DatabaseReference mDatabase;
   private FirebaseAuth firebaseAuth;
    private Button saveB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        EditText Name = (EditText) view.findViewById(R.id.et_profile_name);
        EditText Username = (EditText) view.findViewById(R.id.et_profile_username);
        EditText Phone = (EditText) view.findViewById(R.id.et_profile_phone);
        EditText Email = (EditText) view.findViewById(R.id.et_profile_email);
        Button saveB   = (Button)view.findViewById(R.id.btn_profile_apply);

        String pro_u = user.getUid();
        String pro_e = user.getEmail();



        Email.setText(pro_e);


        mDatabase = FirebaseDatabase.getInstance().getReference();

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

//                            HashMap<String, Object> result = new HashMap<>();
//                            result.put("/username/", Username);

                            mDatabase.child("users").child(user.getUid()).child("username").setValue(Username.getText().toString());

                        }
                    }
                });
            }
        });

        return view; //inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
