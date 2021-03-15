package com.example.splitapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;



public class myAdapter extends FirebaseRecyclerAdapter<post, myAdapter.ViewHolder> {

    private DatabaseReference mDatabase;
    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;


    public myAdapter(@NonNull FirebaseRecyclerOptions<post> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false   );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull post model) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.imageName.setText(model.getPostTitle());
        Glide.with(holder.imageLeft.getContext()).load(model.getLeftURL()).into(holder.imageLeft);
        Glide.with(holder.imageRight.getContext()).load(model.getRightURL()).into(holder.imageRight);
        holder.leftVotes.setText("" + model.getLeftVotes());
        holder.rightVotes.setText("" + model.getRightVotes());
        //holder.leftVotes.setVisibility(View.INVISIBLE);
        //holder.rightVotes.setVisibility(View.INVISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //TODO: Add voting when you click each image
        holder.imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "left image clicked", Toast.LENGTH_SHORT).show();
                holder.leftVotes.setVisibility(View.VISIBLE);
                holder.rightVotes.setVisibility(View.VISIBLE);
                int lVote = Integer.parseInt(holder.leftVotes.getText().toString());
                Log.d(model.getPostTitle(), "click left img: "+ model.getPostTitle() + "-" + model.getUnixTimestamp());
                mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("leftVotes").setValue(lVote+1);
            }
        });

        holder.imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Right image clicked", Toast.LENGTH_SHORT).show();
                holder.leftVotes.setVisibility(View.VISIBLE);
                holder.rightVotes.setVisibility(View.VISIBLE);
                int rVote = Integer.parseInt(holder.rightVotes.getText().toString());
                mDatabase.child("posts").child(model.getPostTitle() + "-" + model.getUnixTimestamp()).child("rightVotes").setValue(rVote+1);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageLeft;
        ImageView imageRight;
        TextView imageName;
        RelativeLayout parentLayout;
        TextView leftVotes;
        TextView rightVotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLeft = itemView.findViewById(R.id.imageLeft);
            imageRight = itemView.findViewById(R.id.imageRight);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            leftVotes = itemView.findViewById(R.id.tvVoteLeft);
            rightVotes = itemView.findViewById(R.id.tvVoteRight);

        }
    }
}
