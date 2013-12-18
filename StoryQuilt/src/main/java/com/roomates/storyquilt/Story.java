package com.roomates.storyquilt;


import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by chris on 12/4/13.
 */
public class Story implements Serializable{
    /**
     * Fields into Firebase
     */
    String id, lastUpdated, title;
    int ageLimit, historyLimit, textLimit;
    long priority;
    HashSet<String> writers;
    ArrayList<Piece> pieces;

    public Story(){} //Firebase required constructor
    public Story(String lastUpdated, String title, int ageLimit, int historyLimit, int textLimit, ArrayList<Piece> pieces, HashSet<String> writers){
        this.lastUpdated = lastUpdated;
        this.title = title;
        this.ageLimit = ageLimit;
        this.historyLimit = historyLimit;
        this.textLimit = textLimit;
        this.pieces = pieces;
        this.writers = writers;
    }

    //Firebase Get Methods
    public String getId(){
        return this.id;
    }
    public String getLastUpdated(){
        return this.lastUpdated;
    }
    public String getTitle(){
        return this.title;
    }
    public int getAgeLimit(){
        return this.ageLimit;
    }
    public int getHistoryLimit(){
        return this.historyLimit;
    }
    public int getTextLimit(){
        return this.textLimit;
    }
    public long getPriority(){
        return this.priority;
    }
    public ArrayList<Piece> getPieces(){
        return this.pieces;
    }
    public HashSet<String> getWriters(){return this.writers;}

    /**
     * Editing the Story
     */
    public void setId(String value){
        this.id = value;
    }
    public void addPiece(Piece newPiece) {
        this.pieces.add(newPiece);
    }
    /**
     * Getting Story Text
     */
    public String fullStory() {
        StringBuilder sb = new StringBuilder();
        for (Piece tempPiece: this.pieces){
            sb.append(tempPiece.text);
            sb.append(" ");
        }
        return sb.toString();
    }
    public String recentPosts() {
        StringBuilder sb = new StringBuilder();
        int wordCount = 0;
        for (int i = this.pieces.size() - 1; i >= 0; i-- ){
            String nextText = pieces.get(i).getText();
            wordCount += nextText.trim().split(" ").length;
            sb.insert(0, nextText.trim());
            if (wordCount > historyLimit){
                break;
            } else {
                sb.insert(0," ");
            }
        }

        for (int i = 0; i < wordCount - historyLimit; i++){
            sb.delete(0, sb.indexOf(" ") + 1);
        }

        return sb.toString();
    }

    /**
     * Check Posting Validity
     */
    public boolean checkMostRecentPoster(User user){
        return this.pieces.get(this.pieces.size()-1).getPoster().equals(user.getEmail());
    }
    public boolean checkWordCount(String str){
        str = str.replaceAll("^[ ]+", "").replaceAll("[ ]+$", "");
        return this.textLimit >= (str.length() - str.replaceAll(" ", "").length()+1);
    }
}