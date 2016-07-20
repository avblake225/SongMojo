package com.tonyblake.songmojo;

public class AvailableFile {

    public String sender;
    public String filename;
    public String recipient;
    public String currentDateAndTime;
    public String duration;
    public String filetype;

    public AvailableFile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AvailableFile(String sender, String filename, String recipient, String currentDateAndTime, String duration, String filetype){

        this.sender = sender;
        this.filename = filename;
        this.recipient = recipient;
        this.currentDateAndTime = currentDateAndTime;
        this.duration = duration;
        this.filetype = filetype;
    }
}
