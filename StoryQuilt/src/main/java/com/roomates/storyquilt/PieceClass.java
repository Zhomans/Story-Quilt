package com.roomates.storyquilt;

import java.io.Serializable;

/**
 * Created by chris on 12/4/13.
 */
public class PieceClass implements Serializable{
    String id, poster, date, text;

    public PieceClass(){} //Firebase required constructor
    public PieceClass(String poster, String date, String text){
        this.poster = poster;
        this.date = date;
        this.text = text;
    }

    //Firebase Get Methods
    public String getId(){
        return this.id;
    }
    public String getPoster(){
        return this.poster;
    }
    public String getDate(){
        return this.date;
    }
    public String getText(){
        return this.text;
    }

    //Setting the id from Firebase
    public void setId(String value){
        this.id = value;
    }
}
