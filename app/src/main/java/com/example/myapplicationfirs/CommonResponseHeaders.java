package com.example.myapplicationfirs;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class CommonResponseHeaders {
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("sid")
    @Expose
    private String sessionId;
    @SerializedName("system_user")
    @Expose
    private String systemUser;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_image")
    @Expose
    private String userImage;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(String systemUser) {
        this.systemUser = systemUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}

