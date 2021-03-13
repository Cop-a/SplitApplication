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

import java.util.ArrayList;



public class myAdapter extends FirebaseRecyclerAdapter<post, myAdapter.ViewHolder> {


    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;

    public myAdapter(@NonNull FirebaseRecyclerOptions<post> options) {
        super(options);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false   );
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull post model) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.imageName.setText(model.getPostTitle());
        Glide.with(holder.imageLeft.getContext()).load(model.getLeftURL()).into(holder.imageLeft);
        Glide.with(holder.imageRight.getContext()).load(model.getRightURL()).into(holder.imageRight);

        //Glide.with(mContext).asBitmap().load(mImagesLeft.get(position)).into(holder.imageLeft);
        //Glide.with(mContext).asBitmap().load(mImagesRight.get(position)).into(holder.imageRight);

        //holder.imageName.setText(mImageNames.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add voting for each post
                Log.d(TAG, "onClick: click on: " + holder.imageName.getText().toString());

                //Toast.makeText(mContext, "test", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /*@Override
    public int getItemCount() {
        return mImageNames.size();
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageLeft;
        ImageView imageRight;
        TextView imageName;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLeft = itemView.findViewById(R.id.imageLeft);
            imageRight = itemView.findViewById(R.id.imageRight);
            imageName = itemView.findViewById(R.id.image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}
