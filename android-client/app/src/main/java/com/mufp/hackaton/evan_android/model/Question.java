package com.mufp.hackaton.evan_android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by milan on 22.11.14..
 */
public class Question implements Serializable{
    private String text;
    private int votes;
    private String id;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public Question(String text, int votes, String id) {
        this.text = text;
        this.votes = votes;
        this.id = id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Question() {
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("votes")
    public int getVotes() {
        return votes;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("votes")
    public void setVotes(int votes) {
        this.votes= votes;
    }
}
