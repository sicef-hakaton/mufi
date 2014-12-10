package com.mufp.hackaton.evan_android.services;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nkzawa.emitter.Emitter;
import com.mufp.hackaton.evan_android.model.DeletedQuestion;
import com.mufp.hackaton.evan_android.model.Poll;
import com.mufp.hackaton.evan_android.model.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by milan on 23.11.14..
 */
public abstract class MainHandler extends ServiceHandler{
    private static final String TAG=MainHandler.class.getSimpleName();

    private static final String UNDERSTANDING_LEVEL="understandingLevel";
    private static final String NEW_QUESTION="newQuestion";
    private static final String POLL="newPoll";


    private static final String UNDERSTANDING_LEVEL_CHANGED="understandingLevelChanged";
    private static final String ADD_NEW_QUESTION="newQuestion";
    private static final String UPVOTE_QUESTION="newVote";
    private static final String DELETE_QUESTION="questionDeleted";
    private static final String POLL_RESPONSE="answerPoll";

    private static final String QUESTION="question";
    private static final String DELETE="delete";
    private static final int QUESTION_RECEIVED=1;
    private static final int DELETED_QUESTION=2;
    private static final int POLL_RECEIVED=3;
    @Override
    public void handleMessage(Message msg) {
        final int what = msg.what;
        Bundle b;
        switch(what) {
            case QUESTION_RECEIVED:
                b=msg.getData();
                Question question=(Question)b.getSerializable(QUESTION);
                questionReceived(question); break;
            case DELETED_QUESTION:
                Log.d(TAG,"Deleting question");
                b=msg.getData();
                DeletedQuestion deleted=(DeletedQuestion)b.getSerializable(DELETE);
                deleteQuestion(deleted.getId()); break;
            case POLL_RECEIVED:
                Log.d(TAG,"Received POLL");
                b=msg.getData();
                Poll poll= (Poll) b.getSerializable(POLL);
                Log.d(TAG, "Choices: "+poll.getNumber());
                poolReceived(poll.getNumber());
                break;
        }
    }

    public Emitter registerService(Emitter emitter){
        final ObjectMapper mapper=new ObjectMapper();

        return emitter.on(NEW_QUESTION, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d(TAG, "Login: Success");
                JSONObject jsonObj = (JSONObject) args[0];
                Log.d(TAG, "result1: " + jsonObj.toString());
                Question question = null;
                try {
                    question = mapper.readValue(jsonObj.toString(), Question.class);
                } catch (IOException e) {
                    Log.d(TAG, "Error parsing");
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(QUESTION, question);
                Message message = new Message();
                message.setData(bundle);
                message.what = QUESTION_RECEIVED;
                sendMessage(message);
            }
        }).on(POLL, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d(TAG, "POLL: Success: "+args[0]);
                JSONObject jsonObj=(JSONObject)args[0];
                Log.d(TAG,"result1 POLL RAW: "+jsonObj.toString());
                Poll poll=null;
                try {
                    poll=mapper.readValue(jsonObj.toString(), Poll.class);
                } catch (IOException e) {
                    Log.d(TAG,"Error parsing POLL");
                    e.printStackTrace();
                }

                Bundle bundle=new Bundle();
                bundle.putSerializable(POLL,poll);
                Message message=new Message();
                message.setData(bundle);
                message.what=POLL_RECEIVED;
                sendMessage(message);
            }
        }).on(DELETE_QUESTION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "DELETE QUESTION: Success: " + args[0]);
                JSONObject jsonObj = (JSONObject) args[0];
                Log.d(TAG, "result1: " + jsonObj.toString());
                DeletedQuestion deleted = null;
                try {
                    deleted = mapper.readValue(jsonObj.toString(), DeletedQuestion.class);
                } catch (IOException e) {
                    Log.d(TAG, "Error parsing");
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(DELETE, deleted);
                Message message = new Message();
                message.setData(bundle);
                message.what = DELETED_QUESTION;
                sendMessage(message);
            }
        });
    }

    @Override
    public void connectionSuccess() {

    }

    @Override
    public void connectionError() {

    }

    public void sendUnderstandingLevel(int level)throws JSONException {
        JSONObject jsonCredentials=new JSONObject();
        jsonCredentials.put("level", level);
        getParent().getSocket().emit(UNDERSTANDING_LEVEL_CHANGED,jsonCredentials);
    }

    public void sendNewQuestion(String questionText)throws JSONException {
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("text", questionText);
        getParent().getSocket().emit(ADD_NEW_QUESTION,jsonObj);
    }

    public void upvoteQuestion(String id)throws JSONException {
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("id", id);
        getParent().getSocket().emit(UPVOTE_QUESTION,jsonObj);
    }

    public void sendPollResult(int choice)throws JSONException {
        Log.d(TAG,"Sending poll result: "+choice);
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("choice", choice);
        getParent().getSocket().emit(POLL_RESPONSE,jsonObj);
    }

    public abstract void questionReceived(Question question);
    public abstract void deleteQuestion(String id);
    public abstract void poolReceived(int choicesNumber);
}
