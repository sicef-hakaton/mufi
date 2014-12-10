package com.mufp.hackaton.evan_android.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mufp.hackaton.evan_android.R;
import com.mufp.hackaton.evan_android.activities.fragments.NewQuestionDialog;
import com.mufp.hackaton.evan_android.activities.fragments.PollFragment;
import com.mufp.hackaton.evan_android.activities.fragments.StatsFragment;
import com.mufp.hackaton.evan_android.model.Question;
import com.mufp.hackaton.evan_android.model.RoomState;
import com.mufp.hackaton.evan_android.services.EvanServices;
import com.mufp.hackaton.evan_android.services.MainHandler;
import com.mufp.hackaton.evan_android.services.SocketService;

import org.json.JSONException;

import java.net.URISyntaxException;

public class MainActivity extends Activity implements PollFragment.OnFragmentInteractionListener, StatsFragment.OnFragmentInteractionListener, NewQuestionDialog.NewQuestionDialogListener, PollFragment.PollQuestionCallback{
    private static final String TAG=MainActivity.class.getSimpleName();

    public static final String ROOM_STATE_PARAM="roomState";

    private boolean mBound=false;
    SocketService mService;
    private EvanServices services;
    private RoomState roomState;
    private StatsFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Created MainActivity");

        Bundle b = getIntent().getExtras();
        roomState =(RoomState) b.getSerializable(ROOM_STATE_PARAM);
        fragment=new StatsFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_question) {
            showNewQuestionDialog();
            return true;
        }

        if (id == R.id.test_pool_dialog) {
            //showPoolDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNewQuestionDialog(){
        FragmentManager fm=getFragmentManager();
        NewQuestionDialog questionDialog=new NewQuestionDialog();
        questionDialog.show(fm, "new_question");
    }

    private void showPoolDialog(int choices){
        FragmentManager fm=getFragmentManager();
        PollFragment questionDialog=PollFragment.newInstance(choices);
        questionDialog.show(fm, "poll");
    }

    @Override
    public RoomState getRoomState() {
        return roomState;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onQuestionVote(Question question) {
        try {
            handler.upvoteQuestion(question.getId());
            Toast.makeText(this, "Voted for selected question.",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.d(TAG, "Error");
        }
    }

    @Override
    public void onUnderstandingLevelChanged(int level) {
        try {
            handler.sendUnderstandingLevel(level);
            Toast.makeText(this, "Your new understanding level has been sent.",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.d(TAG, "Error");
        }
    }

    @Override
    public void newQuestionAdded(String message) {
        Toast.makeText(this,"Message added",Toast.LENGTH_SHORT).show();
        try {
            handler.sendNewQuestion(message);
        } catch (JSONException e) {
            Log.d(TAG, "Error");
        }
    }

    @Override
    public void answeredQuestion(int number) {
        Toast.makeText(this, "Selected: "+number,Toast.LENGTH_SHORT).show();
        try {
            handler.sendPollResult(number);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "Error");
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
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

    MainHandler handler=new MainHandler(){

        @Override
        public void questionReceived(Question question) {
            fragment.addActiveQuestion(question);
        }

        @Override
        public void deleteQuestion(String id) {
            Log.d(TAG,"Deleting: "+id);
            fragment.deleteActiveQuestion(id);
        }

        @Override
        public void poolReceived(int choicesNumber){
            Log.d(TAG,"CHOICES: "+choicesNumber);
            showPoolDialog(choicesNumber);
        }
    };


}
