package com.example.splitapp;
import java.util.ArrayList;
import java.util.Map;

public class post {

    public String uID;
    public String postTitle;
    public String leftURL;
    public String rightURL;
    public long unixTimestamp;
    public int leftVotes;
    public int rightVotes;
    public int totalVotes;

    public post() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public post(String uID, String postTitle, String leftURL, String rightURL, long unixTimestamp, int leftVotes, int rightVotes, int totalVotes) {
        this.uID = uID;
        this.postTitle = postTitle;
        this.leftURL = leftURL;
        this.rightURL = rightURL;
        this.unixTimestamp = unixTimestamp;
        this.leftVotes = leftVotes;
        this.rightVotes = rightVotes;
        this.totalVotes = totalVotes;

    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getLeftURL() {
        return leftURL;
    }

    public void setLeftURL(String leftURL) {
        this.leftURL = leftURL;
    }

    public String getRightURL() {
        return rightURL;
    }

    public void setRightURL(String rightURL) {
        this.rightURL = rightURL;
    }

    public int getLeftVotes() {
        return leftVotes;
    }

    public int getRightVotes() {
        return rightVotes;
    }

    public long getUnixTimestamp() {
        return unixTimestamp;
    }
}
