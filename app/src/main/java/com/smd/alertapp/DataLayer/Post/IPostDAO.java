package com.smd.alertapp.DataLayer.Post;

import com.smd.alertapp.Entities.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface IPostDAO {
    public void save(Post post);

    public Post getById(String id);
    public void getPosts(PostsCallback postsCallback);
}
