package com.smd.alertapp.DataLayer.Post;

import com.smd.alertapp.Entities.Post;

import java.util.ArrayList;

public interface PostsCallback {
    void onPostsReceived(ArrayList<Post> postsList);
}
