package com.roomates.storyquilt;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chris on 12/4/13.
 */
public class Story implements Serializable{
    String id, lastUpdated, title;
    int ageLimit, historyLimit, textLimit;
    long priority;
    ArrayList<Piece> pieces;

    public Story(){} //Firebase required constructor
    public Story(String lastUpdated, String title, int ageLimit, int historyLimit, int textLimit, ArrayList<Piece> pieces){
        this.lastUpdated = lastUpdated;
        this.title = title;
        this.ageLimit = ageLimit;
        this.historyLimit = historyLimit;
        this.textLimit = textLimit;
        this.pieces = pieces;
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

    //Setting the priority based on viewers and posters
    public void setPriority(int num_viewers, int num_posters){
        this.priority = num_posters + num_viewers;
    }

    //Setting the id from Firebase
    public void setId(String value){
        this.id = value;
    }

    //Get Length of Story (by Posts)
    public int getLength() { return this.pieces.size(); }

    //Get Full Text of a Story
    public String getFullStory() {
        String fullText = "";
        for (Piece piece : this.pieces) {
            fullText.concat(piece.getText()+" ");
        }
        return fullText;
    }

    //Get Recent Posts of Story
    //XXX Possibly refactor to make more efficient. Use split?
    public String getRecentPosts() {
        //XXX Could refactor to take in FullStory so it doesn't need to recalculate
        String fullText = this.getFullStory();
        String recentWords = "";
        int textCounter = this.textLimit;
        while (fullText != "" && textCounter != 0) {
            int indexOfFinalSpace = fullText.lastIndexOf(" ");
            recentWords.concat(fullText.substring(indexOfFinalSpace+1));
            fullText.substring(0,indexOfFinalSpace); //XXX Possible One-Off Error. Check this if something is broken.
            textCounter--;
        }
        return recentWords;
    }

    //Add Piece to Story
    public void addPiece(Piece newPiece) {
        this.pieces.add(newPiece);
    }

    //Check Most Recent Post for Given User
    public boolean checkMostRecentPoster(User user){
        return this.pieces.get(this.pieces.size()-1).getPoster().equals(user.getId());
    }
}