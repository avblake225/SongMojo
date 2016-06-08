package com.tonyblake.songmojo;

import android.os.AsyncTask;

import java.util.ArrayList;

public class LoginTask extends AsyncTask<String,Void,User>{

    private String username, password;

    private ArrayList<User> users;

    public LoginTask(ArrayList<User> users){

        this.users = users;
    }

    @Override
    protected User doInBackground(String... params) {

        username = params[0];

        password = params[1];

        User userToReturn = null;

        for(User user: users){

            if(username.equals(user.username) && password.equals(user.password)){

                userToReturn = user;
            }
        }

        return userToReturn;
    }
}
