package com.mufp.hackaton.evan_android.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by milan on 23.11.14..
 */
public class RoomState implements Serializable{
    private String roomName;
    private List<Question> questions;
    private String professorName;

    @JsonProperty("presentation_name")
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @JsonProperty("prof_name")
    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    @JsonProperty("questions")
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @JsonProperty("presentation_name")
    public String getRoomName() {
        return roomName;
    }

    @JsonProperty("prof_name")
    public String getProfessorName() {
        return professorName;
    }

    @JsonProperty("questions")
    public List<Question> getQuestions() {
        return questions;
    }
}
