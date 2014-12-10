package com.mufp.hackaton.evan_android.services;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.mufp.hackaton.evan_android.activities.Settings;

import java.net.URISyntaxException;

/**
 * Created by milan on 23.11.14..
 */
public class SocketService extends Service {
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private Socket socket;

    private String url;

    public SocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        url =sharedPref.getString(Settings.SERVER_ADDRESS_KEY,"");
        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SocketService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */
    public Socket getSocket() {
        return socket;
    }
}
