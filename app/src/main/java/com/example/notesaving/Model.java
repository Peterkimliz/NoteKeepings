package com.example.notesaving;

public class Model {

    String message;
    String date;
    String title;
    String image;

    public Model(String message, String date, String title, String image) {
        this.message = message;
        this.date = date;
        this.title = title;
        this.image = image;
    }

    public Model() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
