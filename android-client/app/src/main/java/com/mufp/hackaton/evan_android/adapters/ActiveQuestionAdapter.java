package com.mufp.hackaton.evan_android.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mufp.hackaton.evan_android.R;
import com.mufp.hackaton.evan_android.model.Question;

import java.util.ArrayList;

/**
 * Created by milan on 22.11.14..
 */
public class ActiveQuestionAdapter extends BaseAdapter implements View.OnClickListener{
    private  static final String TAG=ActiveQuestionAdapter.class.getSimpleName();
    public static  interface Callback{
        public Activity getActivity();
        public void onItemClick(int position);

    }

    /**Variables*/
    private Callback parent;
    private ArrayList<Question> data;
    private static LayoutInflater inflater=null;
    public Resources res;
    Question tmpValue;
    int i=0;

    public ActiveQuestionAdapter(Callback parent, ArrayList<Question> data, Resources res) {
        this.parent = parent;
        this.data = data;
        this.res = res;

        inflater=(LayoutInflater)parent.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data.size()<0){
            return 0;
        }
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView questionText;
        public ImageButton voteButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.question_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.questionText = (TextView) vi.findViewById(R.id.txtQuestionText);
            holder.voteButton=(ImageButton)vi.findViewById(R.id.btnQuestionVote);
            holder.voteButton.setOnClickListener(new OnVoteClickListener(position));

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.questionText.setText("No data");
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tmpValue=null;
            tmpValue= (Question) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.questionText.setText( tmpValue.getText() );

            /******** Set Item Click Listner for LayoutInflater for each row *******/
            holder.voteButton.setOnClickListener(new OnItemClickListener( position, holder.voteButton ));
            //vi.setOnClickListener(new OnItemClickListener( position ));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.d("Question adapter","Pressed on item");
    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;
        private ImageButton btn;

        OnItemClickListener(int position, ImageButton btn){
            mPosition = position;
            this.btn=btn;
        }

        @Override
        public void onClick(View arg0) {

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            parent.onItemClick(mPosition);
        }
    }

    private class OnVoteClickListener  implements View.OnClickListener {
        private int mPosition;

        OnVoteClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            Log.d(TAG,"Clicked on vote button");
            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
            parent.onItemClick(mPosition);
        }
    }
}
