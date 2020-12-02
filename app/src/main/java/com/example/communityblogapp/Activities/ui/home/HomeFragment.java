package com.example.communityblogapp.Activities.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityblogapp.Adapters.PostAdapter;
import com.example.communityblogapp.Models.Post;
import com.example.communityblogapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<Post> postList;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_home,container,false);
        postRecyclerView = fragmentView.findViewById(R.id.postRV);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        return fragmentView;

        /*homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;*/
    }

    @Override
    public void onStart() {
        super.onStart();
        //get list posts from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList = new ArrayList<>();
                for(DataSnapshot postsnap: snapshot.getChildren()){
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter = new PostAdapter(getActivity(),postList);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}