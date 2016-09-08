package com.tonyblake.songmojo;

public class FileReceived {

    public String dateAndTime;
    public String sender;
    public String filename;
    public String filetype;
    public String duration;

    public FileReceived(String dateAndTime, String sender, String filename, String filetype, String duration){

        this.dateAndTime = dateAndTime;
        this.sender = sender;
        this.filename = filename;
        this.filetype = filetype;
        this.duration = duration;
    }
}
