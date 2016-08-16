package com.tonyblake.songmojo;

public class User {

    public String firstName;
    public String lastName;
    public String fullName;
    public String email;
    public String password;

    public User(){}

    public User(String firstName, String lastName, String fullName, String email, String password){

        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
}
