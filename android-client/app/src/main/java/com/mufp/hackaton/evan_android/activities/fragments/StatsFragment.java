package com.mufp.hackaton.evan_android.activities.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mufp.hackaton.evan_android.R;
import com.mufp.hackaton.evan_android.adapters.ActiveQuestionAdapter;
import com.mufp.hackaton.evan_android.model.Question;
import com.mufp.hackaton.evan_android.model.RoomState;

import java.util.ArrayList;

public class StatsFragment extends Fragment implements ActiveQuestionAdapter.Callback{
    private static final String TAG=StatsFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String ROOM_STATE="roomState";

    static class UnderstandingDescription extends Pair<Integer, String>{
        UnderstandingDescription(Integer maxValue, String name){
            super(maxValue,name);
        }
    }

    private ArrayList<UnderstandingDescription> understandingHint=new ArrayList<UnderstandingDescription>(){{
        add(new UnderstandingDescription(80, "PERFECT"));
        add(new UnderstandingDescription(60, "GOOD"));
        add(new UnderstandingDescription(40, "SEMI"));
        add(new UnderstandingDescription(20, "BAD"));
        add(new UnderstandingDescription(0, "POOR"));
    }};

    private SeekBar seekUnderstandingLevel;
    private ListView list;
    private ActiveQuestionAdapter adapter;
    public ActiveQuestionAdapter.Callback CustomListView=null;
    public  ArrayList<Question> ActiveQuestionsValues = new ArrayList<Question>();
    private Activity parent;
    private RoomState roomState;

    private TextView txtRoomName;
    private TextView txtProfessroName;
    private TextView txtUnderstandingHint;


    private OnFragmentInteractionListener mListener;

    public static StatsFragment newInstance(RoomState roomState) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ROOM_STATE, roomState);
        fragment.setArguments(args);
        return fragment;
    }

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"On create");
        if (getArguments() != null) {
            Log.d(TAG,"get arguments");
        }

        roomState=mListener.getRoomState();
        setListData();
    }

    private void setListData(){
        for (Question q: roomState.getQuestions()){
            ActiveQuestionsValues.add(0,q);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_stats, container, false);
        seekUnderstandingLevel= (SeekBar) view.findViewById(R.id.seekUnderstandingLevel);
        seekUnderstandingLevel.setMax(100);
        seekUnderstandingLevel.setMinimumHeight(0);
        seekUnderstandingLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progress;


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                for (UnderstandingDescription d:understandingHint){
                    Log.d(TAG,"Progress: "+progress+" "+d.first);
                    if (progress>=d.first){
                        txtUnderstandingHint.setText(d.second);
                        break;
                    }
                }
                mListener.onUnderstandingLevelChanged(progress);
            }
        });

        txtProfessroName= (TextView) view.findViewById(R.id.txtProffesorName);
        txtRoomName=(TextView)view.findViewById(R.id.txtRoomName);
        txtUnderstandingHint=(TextView)view.findViewById(R.id.txtUnderstandingHint);

        txtRoomName.setText(roomState.getRoomName());
        txtProfessroName.setText(roomState.getProfessorName());

        Resources res=getResources();
        list=(ListView)view.findViewById(R.id.listView);
        adapter=new ActiveQuestionAdapter(CustomListView, ActiveQuestionsValues, res);
        list.setAdapter(adapter);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void addActiveQuestion(Question newQuestion){
        Log.d(TAG, "Received question: "+newQuestion.getText());
        ActiveQuestionsValues.add(0,newQuestion);
        adapter.notifyDataSetChanged();
    }

    public void deleteActiveQuestion(String id){
        Log.d(TAG, "Delete question: "+id);
        for (int i=0;i<ActiveQuestionsValues.size();i++){
            if (ActiveQuestionsValues.get(i).getId().equals(id)){
                Log.d(TAG,"DELETIG");
                ActiveQuestionsValues.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;

            parent=activity;
            CustomListView =this;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void poolReceived(int choices){

    }

    public Activity getParentActivity(){
        return parent;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(int position) {
        ActiveQuestionAdapter adp= (ActiveQuestionAdapter) list.getAdapter();
        Question question= (Question) adp.getItem(position);
        mListener.onQuestionVote(question);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void onQuestionVote(Question question);
        public void onUnderstandingLevelChanged(int level);
        public RoomState getRoomState();
    }

}
