package com.smd.alertapp.DataLayer.Post;

public interface FileUploadCallback {
    void onFileUpload(String fileUrl);

    void onError(String message);
}
