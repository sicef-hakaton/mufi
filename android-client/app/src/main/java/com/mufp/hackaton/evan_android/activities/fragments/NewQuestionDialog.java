package com.mufp.hackaton.evan_android.activities.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mufp.hackaton.evan_android.R;

/**
 * Created by milan on 23.11.14..
 */
public class NewQuestionDialog extends DialogFragment implements TextView.OnEditorActionListener{
    private EditText txtQuestion;

    public NewQuestionDialog(){

    }

    public interface NewQuestionDialogListener{
        void newQuestionAdded(String message);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b=  new  AlertDialog.Builder(getActivity())
                .setTitle("Ask a question")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                NewQuestionDialogListener activity = (NewQuestionDialogListener) getActivity();
                                if (txtQuestion.getText().toString()!="") {
                                    activity.newQuestionAdded(txtQuestion.getText().toString());
                                    NewQuestionDialog.this.dismiss();
                                    return;
                                }
                            }
                        }
                );
        LayoutInflater i=getActivity().getLayoutInflater();

        View view=i.inflate(R.layout.fragment_new_question,null);
        txtQuestion = (EditText) view.findViewById(R.id.txt_your_name);
        b.setView(view);

        return b.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= super.onCreateView(inflater, container, savedInstanceState);
        txtQuestion.requestFocus();
        getDialog().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return  view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            NewQuestionDialogListener activity = (NewQuestionDialogListener) getActivity();
            activity.newQuestionAdded(txtQuestion.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }
}
