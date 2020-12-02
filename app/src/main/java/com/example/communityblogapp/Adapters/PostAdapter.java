package com.example.communityblogapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.communityblogapp.Activities.PostDetailActivity;
import com.example.communityblogapp.Models.Post;
import com.example.communityblogapp.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mComtext;
    List<Post> mData;

    public PostAdapter(Context mComtext, List<Post> mData) {
        this.mComtext = mComtext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mComtext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.row_post_title.setText(mData.get(position).getTitle());
        Glide.with(mComtext).load(mData.get(position).getPicture()).into(holder.row_post_img);
        Glide.with(mComtext).load(mData.get(position).getUserPhoto()).into(holder.row_post_profile_img);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView row_post_title;
        ImageView row_post_img;
        ImageView row_post_profile_img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            row_post_title = itemView.findViewById(R.id.row_post_title);
            row_post_img = itemView.findViewById(R.id.row_post_img);
            row_post_profile_img = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mComtext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
                    //postDetailActivity.putExtra("userName",mData.get(position).getUserName());

                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mComtext.startActivity(postDetailActivity);
                }
            });
        }
    }
}
