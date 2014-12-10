package com.mufp.hackaton.evan_android.services;

import android.os.Handler;
import android.os.Message;

import com.github.nkzawa.emitter.Emitter;

/**
 * Created by milan on 23.11.14..
 */
public abstract class ServiceHandler extends Handler {
    public final static int CONNECTION_SUCCESS = 0;
    public final static int CONNECTION_FAIL = 1;

    private EvanServices parent;

    protected EvanServices getParent(){
        return parent;
    }

    void setEvanService(EvanServices services){
        this.parent=services;
    }

    @Override
    public void handleMessage(Message msg) {
        final int what = msg.what;
        switch(what) {
            case CONNECTION_SUCCESS: connectionSuccess(); break;
            case CONNECTION_FAIL: connectionError(); break;
        }
    }

    public abstract Emitter registerService(Emitter emitter);

    public abstract void connectionSuccess();

    public abstract void connectionError();


}
