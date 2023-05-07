package com.smd.alertapp.Entities;

public class Comment {
    private String commentId;
    private String postId;
    private String commentText;
    private String commentUser;

    public String getCommentId() {
        return commentId;
    }

    public Comment() {

    }

    public Comment(String pid, String user, String text) {
        postId = pid;
        commentText = text;
        commentUser = user;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }
}
