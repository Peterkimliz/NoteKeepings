package com.example.notesaving;

public class Model {

    String message;
    String date;
    String title;

    public Model() {
    }

    public Model( String message, String date, String title) {

        this.message = message;
        this.date = date;
        this.title = title;
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
}
