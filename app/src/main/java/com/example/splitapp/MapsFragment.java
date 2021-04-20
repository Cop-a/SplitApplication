package com.example.splitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.Int32Value;

public class MapsFragment extends Fragment {

    String value;
    String latString = "null";
    String lonString = "null";


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String latS = "null";
    double lat;
    String lonS;
    double lon;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {


            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
/*
            mDatabase.child("posts").child(value).child("latitude").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebaseTEST", String.valueOf(task.getResult().getValue()));

                        latS = String.valueOf(task.getResult().getValue());
                        Log.d("Lat After (INSIDE):", latS);
                        lat = Double.parseDouble(latS);

                    }
                }
            });
*/
            mDatabase.child("posts").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        latString = snapshot.child("latitude").getValue(String.class);
                        lonString = snapshot.child("longitude").getValue(String.class);
                        Log.d("Lat After (INSIDE):", latString);

                        lat = Double.parseDouble(latString);
                        lon = Double.parseDouble(lonString);

                        LatLng location = new LatLng(lat, lon);
                        googleMap.addMarker(new MarkerOptions().position(location).title("Location of Post"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        value = getArguments().getString("key");
        Log.d("Key:", value);


        return inflater.inflate(R.layout.fragment_maps2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}