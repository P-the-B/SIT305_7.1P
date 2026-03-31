package com.example.lostfound.model;

// Model representing a single lost or found item posting
public class LostItem {

    private int id;
    private String title;
    private String type;
    private String name;
    private String phone;
    private String description;
    private String date;
    private String location;
    private String imageUri;
    private String timestamp;

    public LostItem() {}

    public LostItem(String title, String type, String name, String phone,
                    String description, String date, String location,
                    String imageUri, String timestamp) {

        this.title       = title;
        this.type        = type;
        this.name        = name;
        this.phone       = phone;
        this.description = description;
        this.date        = date;
        this.location    = location;
        this.imageUri    = imageUri;
        this.timestamp   = timestamp;
    }

    // Getters
    public int    getId()          { return id; }
    public String getTitle()       { return title; }
    public String getType()        { return type; }
    public String getName()        { return name; }
    public String getPhone()       { return phone; }
    public String getDescription() { return description; }
    public String getDate()        { return date; }
    public String getLocation()    { return location; }
    public String getImageUri()    { return imageUri; }
    public String getTimestamp()   { return timestamp; }

    // Setters
    public void setId(int id)                   { this.id = id; }
    public void setTitle(String title)          { this.title = title; }
    public void setType(String type)            { this.type = type; }
    public void setName(String name)            { this.name = name; }
    public void setPhone(String phone)          { this.phone = phone; }
    public void setDescription(String desc)     { this.description = desc; }
    public void setDate(String date)            { this.date = date; }
    public void setLocation(String location)    { this.location = location; }
    public void setImageUri(String imageUri)    { this.imageUri = imageUri; }
    public void setTimestamp(String timestamp)  { this.timestamp = timestamp; }
}