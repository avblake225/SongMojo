package com.tonyblake.songmojo;

public class FileSent {

    public String token;
    public String sender;
    public String recipient;
    public String filename;
    public String filetype;
    public String duration;

    public FileSent(String token, String sender, String recipient, String filename, String filetype, String duration){

        this.token = token;
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
        this.filetype = filetype;
        this.duration = duration;
    }
}
