package com.mufp.hackaton.evan_android.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mufp.hackaton.evan_android.R;
import com.mufp.hackaton.evan_android.model.RoomCredentials;
import com.mufp.hackaton.evan_android.model.RoomState;
import com.mufp.hackaton.evan_android.services.EvanServices;
import com.mufp.hackaton.evan_android.services.LoginHandler;
import com.mufp.hackaton.evan_android.services.SocketService;

import org.json.JSONException;

import java.net.URISyntaxException;


public class LoginActivity extends Activity{
    private static final String TAG=LoginActivity.class.getSimpleName();

    private Button loginButton;
    private EvanServices services;

    private TextView roomName;
    private TextView roomPassword;

    private boolean mBound=false;
    SocketService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton=(Button)findViewById(R.id.loginBtnLogin);
        loginButton.setOnClickListener(btnLoginListener);
        roomName=(TextView)findViewById(R.id.loginRoomName);
        roomPassword=(TextView)findViewById(R.id.loginRoomPassword);


    }

    LoginHandler handler=new LoginHandler() {
        @Override
        public void connectionSuccess() {
            Toast.makeText(LoginActivity.this,"Connected to server!",Toast.LENGTH_LONG).show();
        }

        @Override
        public void connectionError() {
            Toast.makeText(LoginActivity.this,"Error connecting",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void loginSuccess(RoomState roomState) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putSerializable(MainActivity.ROOM_STATE_PARAM, roomState);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }

        @Override
        public void loginFail(String message) {
            Toast.makeText(LoginActivity.this,message,Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=new Intent(this, SocketService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            try {
                services=new EvanServices(handler,mService.getSocket());
                services.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private View.OnClickListener btnLoginListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            try {
                handler.login(new RoomCredentials(roomName.getText().toString(),roomPassword.getText().toString()));
            } catch (JSONException e) {
                Log.d(TAG, "Error logging in");
            }
        }
    };
}
