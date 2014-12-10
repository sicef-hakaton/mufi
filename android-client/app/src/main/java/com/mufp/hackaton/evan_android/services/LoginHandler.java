package com.mufp.hackaton.evan_android.services;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nkzawa.emitter.Emitter;
import com.mufp.hackaton.evan_android.model.RoomCredentials;
import com.mufp.hackaton.evan_android.model.RoomState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by milan on 22.11.14..
 */

public abstract class LoginHandler extends ServiceHandler{
    private static final String TAG=LoginHandler.class.getSimpleName();

    public final static int LOGIN_SUCCESS = 2;
    public final static int LOGIN_FAIL = 3;

    private static final String SUCCESS="success";
    private static final String FAIL="fail";
    private static final String LOGIN="joinStud";

    private static final String ROOM_STATE="roomState";

    @Override
    public void handleMessage(Message msg) {
        final int what = msg.what;
        switch(what) {
            case LOGIN_SUCCESS:
                Log.d(TAG,"Dispatching message");
                Bundle b=msg.getData();
                RoomState roomState=(RoomState)b.getSerializable(ROOM_STATE);
                loginSuccess(roomState); break;
            case LOGIN_FAIL: loginFail("Error logging ins"); break;
        }
    }

    public Emitter registerService(Emitter emitter){
        final ObjectMapper mapper=new ObjectMapper();
        return emitter.on(SUCCESS, new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                Log.d(TAG, "Login: Success");

                JSONObject jsonObj=(JSONObject)args[0];
                Log.d(TAG,"result1: "+jsonObj.toString());
                RoomState roomState=null;
                try {
                    roomState=mapper.readValue(jsonObj.toString(), RoomState.class);
                } catch (IOException e) {
                    Log.d(TAG,"Error parsing");
                    e.printStackTrace();
                }

                Bundle bundle=new Bundle();
                bundle.putSerializable(ROOM_STATE,roomState);
                Message message=new Message();
                message.setData(bundle);
                message.what=LOGIN_SUCCESS;
                sendMessage(message);
            }
        }).on(FAIL, new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                Log.d(TAG, "Login: FAIL!");
                sendEmptyMessage(LOGIN_FAIL);
            }
        });
    }

    public void login(RoomCredentials credentials) throws JSONException {
        JSONObject jsonCredentials=new JSONObject();
        jsonCredentials.put("roomName",credentials.getRoomName());
        jsonCredentials.put("roomPassword",credentials.getRoomPassword());
        getParent().getSocket().emit(LOGIN,jsonCredentials);
    }

    public abstract void loginSuccess(RoomState roomState);
    public abstract void loginFail(String message);
}
