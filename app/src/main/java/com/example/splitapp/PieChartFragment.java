package com.example.splitapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import de.hdodenhof.circleimageview.CircleImageView;


public class PieChartFragment extends Fragment {

    private DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String value;
    PieChart pieChart;
    View view;
    int left, right;
    boolean yo = false;
    int [] color={ Color.rgb(234, 83, 87), Color.rgb(0, 136, 204), Color.rgb(255,136,0),
            Color.rgb(255,0,0), Color.rgb(255,127,80), Color.rgb(47,95,255)
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_piechart, container, false);
        value = getArguments().getString("key");
        pieChart = view.findViewById(R.id.PieChart);
        ArrayList<PieEntry> votes = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();



            mDatabase.child("posts").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        left = snapshot.child("leftVotes").getValue(int.class);
                        right = snapshot.child("rightVotes").getValue(int.class);
                        Log.d("Hello:", String.valueOf(left));
                        Log.d("Hello:", String.valueOf(right));

                        votes.add(new PieEntry(left*-1, "Left Votes"));
                        votes.add(new PieEntry(right*-1, "Right Votes"));

                        PieDataSet pieDataSet = new PieDataSet(votes, "");
                        pieDataSet.setColors(color);

                        pieDataSet.setValueTextColor(Color.BLACK);
                        pieDataSet.setValueTextSize(16f);
                        Legend legend = pieChart.getLegend();
                        legend.setTextSize(20f);
                        legend.setTypeface(Typeface.DEFAULT_BOLD);


                        PieData pieData = new PieData(pieDataSet);

                        float friction = 0.95f;
                        pieChart.setDragDecelerationFrictionCoef(friction);
                        pieChart.setData(pieData);
                        pieChart.invalidate();
                        pieChart.getDescription().setText("");

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });












        // Inflate the layout for this fragment
        return view;
    }


}