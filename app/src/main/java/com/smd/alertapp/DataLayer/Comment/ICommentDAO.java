package com.smd.alertapp.DataLayer.Comment;

import com.smd.alertapp.DataLayer.Post.PostsCallback;
import com.smd.alertapp.Entities.Comment;
import com.smd.alertapp.Entities.Post;

public interface ICommentDAO {
    public void save(Comment comment);

    public void getComments(CommentCallback commentsCallback, String postId);
}
