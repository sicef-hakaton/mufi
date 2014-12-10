package com.mufp.hackaton.evan_android.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mufp.hackaton.evan_android.R;

import java.util.ArrayList;

public class PollFragment extends DialogFragment {
    private static final String PARAM_CHOICE_NUMBER = "numberOfChoices";

    public interface PollQuestionCallback{
        void answeredQuestion(int number);
    }

    private int choiceNumber=2;

    private OnFragmentInteractionListener mListener;
    private ArrayList<RadioButton> rbChoices=new ArrayList<RadioButton>();

    public static PollFragment newInstance(int choiceNumber) {
        PollFragment fragment = new PollFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_CHOICE_NUMBER, choiceNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PollFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            choiceNumber = getArguments().getInt(PARAM_CHOICE_NUMBER);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b=  new  AlertDialog.Builder(getActivity())
                .setTitle("Answer a question")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                PollQuestionCallback activity = (PollQuestionCallback) getActivity();
                                activity.answeredQuestion(getSelectedItem());
                                PollFragment.this.dismiss();
                                return;
                            }
                        }
                );
        LayoutInflater i=getActivity().getLayoutInflater();

        View view=i.inflate(R.layout.fragment_poll,null);

        Log.d("PollFragment", "ACTIVITY: " + getActivity());
        RadioGroup holder= (RadioGroup) view.findViewById(R.id.layoutHolder);

        for (int j=0;j<choiceNumber;j++){
            RadioButton rb=new RadioButton(getActivity());
            rb.setText("Answer: "+(j+1));
            holder.addView(rb);
            rbChoices.add(rb);
        }

        b.setView(view);
        Log.d("PollFragment","Created");
        return b.create();
    }

    private int getSelectedItem(){
        for(int i=0;i<rbChoices.size();i++){
            if (rbChoices.get(i).isChecked()){
                return i+1;
            }
        }
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return  view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
