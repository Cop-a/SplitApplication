package com.example.splitapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class FeedFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    myAdapter adapter;
    RecyclerView recyclerView;
    Query query;



    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");

        view = inflater.inflate(R.layout.fragment_feed, container, false);
        Spinner spinner = view.findViewById(R.id.filterSpinner);
        recyclerView = view.findViewById(R.id.recyclerv_view);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.filterTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        initRecyclerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerView");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("totalVotes");

        FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(query, post.class).build();

        adapter = new myAdapter(options, getActivity());
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Sort POPULAR
        if (position == 0)
        {
            query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("totalVotes");
            String selected = (String) parent.getItemAtPosition(position);
            //Toast.makeText(getContext(), selected, Toast.LENGTH_SHORT).show();
            FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(query, post.class).build();
            adapter = new myAdapter(options, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.startListening();
            adapter.notifyDataSetChanged();

        }
        //Sort DATE
        else if (position == 1)
        {
            query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("unixTimestamp");
            FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(query, post.class).build();
            adapter = new myAdapter(options, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.startListening();
            adapter.notifyDataSetChanged();
        }
        //Sort PROFILE //TODO: not working need to change unix maybe
        //Prototype: sort by controversial/heated (difference in votes are close to 0)
//        else if (position == 2)
//        {
//            FirebaseAuth firebaseAuth;
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("uID").equalTo(user.getUid());
//            FirebaseRecyclerOptions<post> options = new FirebaseRecyclerOptions.Builder<post>().setQuery(query, post.class).build();
//            adapter = new myAdapter(options, getActivity());
//            recyclerView.setAdapter(adapter);
//            adapter.startListening();
//            adapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("totalVotes");
    }
}
