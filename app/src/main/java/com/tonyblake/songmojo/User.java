package com.tonyblake.songmojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public int id;
    public String username;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(int id, String username, String password){

        this.id = id;
        this.username = username;
        this.password = password;
    }
}
