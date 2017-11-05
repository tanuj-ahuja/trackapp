package com.example.android.trackme.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.trackme.R;
import com.example.android.trackme.model.MeetingDetails;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.client.Query;


/**
 * Created by tanujanuj on 06/10/17.
 */


public class MeetingsListAdapter extends FirebaseListAdapter<MeetingDetails> {

    public MeetingsListAdapter(Activity activity, Class<MeetingDetails> modelClass, int modelLayout,
                             Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }


    @Override
    protected void populateView(View view, MeetingDetails list) {

        /**
         * Grab the needed Textivews and strings
         */
                TextView textViewDate = (TextView) view.findViewById(R.id.text_view_list_name);
                TextView textViewActivity = (TextView) view.findViewById(R.id.text_view_created_by_user);
                TextView textViewTime = (TextView) view.findViewById(R.id.text_view_edit_time);
                TextView textViewClientName = (TextView) view.findViewById(R.id.created_by);

        /* Set the list name and owner */

                textViewDate.setText(list.getDate());
                textViewActivity.setText(list.getActivity());
                textViewTime.setText(list.getTime());
                textViewClientName.setText(list.getClientn());
                Log.d("s", list.getActivity());



    }

}

