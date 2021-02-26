package com.example.splitapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {

   /* private EditText Name;
    private EditText Username;
    private EditText Phone;
    private EditText Biography;
    private EditText Email;
*/
   private FirebaseAuth firebaseAuth;

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

        String pro_u = user.getUid();
        String pro_e = user.getEmail();

        Email.setText(pro_e);
        Username.setText(pro_u);



        return view; //inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
