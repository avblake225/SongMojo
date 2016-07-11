package com.tonyblake.songmojo;

public class AvailableFile {

    public String sender;
    public String filename;
    public String recipient;

    public AvailableFile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AvailableFile(String sender, String filename, String recipient){

        this.sender = sender;
        this.filename = filename;
        this.recipient = recipient;
    }
}
