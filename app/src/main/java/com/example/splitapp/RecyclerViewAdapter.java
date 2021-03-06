package com.example.splitapp;

import android.content.Context;
import android.media.Image;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImagesLeft = new ArrayList<>();
    private ArrayList<String> mImagesRight = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mImageNames, ArrayList<String> mImagesLeft, ArrayList<String> mImagesRight, Context mContext) {
        this.mImageNames = mImageNames;
        this.mImagesLeft = mImagesLeft;
        this.mImagesRight = mImagesRight;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext).asBitmap().load(mImagesLeft.get(position)).into(holder.imageLeft);
        Glide.with(mContext).asBitmap().load(mImagesRight.get(position)).into(holder.imageRight);

        holder.imageName.setText(mImageNames.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: click on: " + mImageNames.get(position));

                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

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
