package com.roomates.storyquilt;

/**
 * Created by chris on 12/4/13.
 */
public class PieceClass {
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
}
