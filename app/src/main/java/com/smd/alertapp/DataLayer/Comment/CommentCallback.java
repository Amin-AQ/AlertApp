package com.smd.alertapp.DataLayer.Comment;

import com.smd.alertapp.Entities.Comment;

import java.util.ArrayList;

public interface CommentCallback {
    void onCommentsReceived(ArrayList<Comment> commentList);
}
