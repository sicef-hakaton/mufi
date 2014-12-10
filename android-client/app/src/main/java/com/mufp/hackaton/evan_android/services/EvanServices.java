package com.mufp.hackaton.evan_android.services;

import android.os.AsyncTask;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.security.InvalidParameterException;

/**
 * Created by milan on 22.11.14..
 */
public class EvanServices {
    private static final String TAG=EvanServices.class.getSimpleName();
    private Socket socket;

    Socket getSocket(){
        return socket;
    }

    public static interface Callback{
        void connectionSuccess();
        void connectionError(String message);
    }

    private ServiceHandler handler=null;

    public EvanServices(final ServiceHandler handler, Socket socket) throws URISyntaxException, InvalidParameterException {
        if (handler!=null)
            this.handler=handler;
        else
        throw new InvalidParameterException("Parent can't be null");
        this.socket = socket;

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d(TAG,"Connection: success");
                handler.sendEmptyMessage(ServiceHandler.CONNECTION_SUCCESS);
            }

        })
        .on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Log.d(TAG,"Connection: fail");
                handler.sendEmptyMessage(ServiceHandler.CONNECTION_FAIL);
            }
        });

        handler.registerService(socket);
        handler.setEvanService(this);
    }

    public void connect(){
        NetworkWorker networkWorker=new NetworkWorker();
        networkWorker.execute();
    }

    private class NetworkWorker extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG,"Sending message");
            socket.connect();
            return  null;
        }
    };

}
