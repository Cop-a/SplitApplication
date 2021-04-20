package com.example.splitapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Boolean.TRUE;


public class myAdapter extends FirebaseRecyclerAdapter<post, myAdapter.ViewHolder> {

    private DatabaseReference mDatabase;
    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    double lon, lat;
    String lonS, latS;


    private static AppCompatActivity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (AppCompatActivity) context;
    }

    public myAdapter(@NonNull FirebaseRecyclerOptions<post> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false   );

        mDatabase = FirebaseDatabase.getInstance().getReference();



        return new ViewHolder(view);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull post model) {
        Log.d(TAG, "onBindViewHolder: called.");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        holder.imageName.setText(model.getPostTitle());
        Glide.with(holder.imageLeft.getContext()).load(model.getLeftURL()).into(holder.imageLeft);
        Glide.with(holder.imageRight.getContext()).load(model.getRightURL()).into(holder.imageRight);

        mDatabase.child("users").child(model.getuID()).child("profileUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    final String picUrl = snapshot.getValue(String.class);
                    Glide.with(holder.postPfp.getContext()).load(picUrl).into(holder.postPfp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

       mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("locationBool").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                   // boolean location2 = snapshot.getValue(boolean.class);
                        holder.location.setVisibility(View.VISIBLE);
                }
                else{
                   // boolean location2 = snapshot.getValue(boolean.class);
                        holder.location.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });



        mDatabase.child("users").child(model.getuID()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    holder.postUsername.setText(snapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        holder.leftVotes.setText("" + model.getLeftVotes() * -1);
        holder.rightVotes.setText("" + model.getRightVotes() * -1);

        holder.postPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // AppCompatActivity activity = (AppCompatActivity) v.getContext();
                AppCompatActivity activity = unwrap(v.getContext());
                Fragment OtherProfileFragment = new OtherProfileFragment();

                Bundle bundle = new Bundle();
                bundle.putString("key", model.getuID());
                OtherProfileFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, OtherProfileFragment).addToBackStack(null).commit();
            }
        });

        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = unwrap(v.getContext());
                Fragment MapsFragment = new MapsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("key", model.getPostTitle() + "-" + model.getUnixTimestamp());
                MapsFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MapsFragment).addToBackStack(null).commit();


            }
        });

        holder.imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast.makeText(mContext, "left image clicked", Toast.LENGTH_SHORT).show();

                String temp = user.getUid();

                mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("hasVoted").child(temp).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (String.valueOf(task.getResult().getValue()) == "null") {
                            Log.d("firebase:", "VOTE");
                            mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("hasVoted").child(temp).setValue(temp);
                            int lVote = Integer.parseInt(holder.leftVotes.getText().toString());
                            int rVote = Integer.parseInt(holder.rightVotes.getText().toString());
                            lVote = lVote*-1;
                            rVote = rVote*-1;
                            mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("leftVotes").setValue(lVote - 1);
                            mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("totalVotes").setValue(rVote + lVote -1);
                        }
                        else {
                            Log.d("firebase", "cant vote" + String.valueOf(task.getResult().getValue()));

                        }
                    }
                });



            }
        });

        holder.imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast.makeText(mContext, "Right image clicked", Toast.LENGTH_SHORT).show();



                String temp = user.getUid();

                mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("hasVoted").child(temp).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (String.valueOf(task.getResult().getValue()) == "null") {
                            Log.d("firebase:", "VOTE");
                            mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("hasVoted").child(temp).setValue(temp);
                            int lVote = Integer.parseInt(holder.leftVotes.getText().toString()); //-1
                            int rVote = Integer.parseInt(holder.rightVotes.getText().toString()); //0
                            lVote = lVote*-1;
                            rVote = rVote*-1;
                            mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("rightVotes").setValue(rVote - 1);
                            Log.d("FUCK", rVote + " " + lVote);
                            mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("totalVotes").setValue(rVote + lVote - 1); //-1 + 0 - 1
                        }
                        else {
                            Log.d("firebase", "cant vote" + String.valueOf(task.getResult().getValue()));

                        }
                    }
                });
            }

        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

    ImageView imageLeft;
    ImageView imageRight;
    CircleImageView postPfp;
    TextView postUsername;
    TextView imageName;
    RelativeLayout parentLayout;
    TextView leftVotes;
    TextView rightVotes;
    ImageView location;



    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        imageLeft = itemView.findViewById(R.id.imageLeft);
        imageRight = itemView.findViewById(R.id.imageRight);
        imageName = itemView.findViewById(R.id.image_name);
        postPfp = itemView.findViewById(R.id.iv_post_pfp);
        postUsername = itemView.findViewById(R.id.tv_username);
        parentLayout = itemView.findViewById(R.id.parent_layout);
        leftVotes = itemView.findViewById(R.id.tvVoteLeft);
        rightVotes = itemView.findViewById(R.id.tvVoteRight);
        location = itemView.findViewById(R.id.iv_location);




    }
}}
