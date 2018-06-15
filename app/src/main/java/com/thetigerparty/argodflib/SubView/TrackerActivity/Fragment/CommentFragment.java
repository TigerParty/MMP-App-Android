package com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.TrackerActivity;

import org.json.JSONException;

/**
 * Created by fredtsao on 2017/8/24.
 */

public class CommentFragment extends Fragment {
    private final static String TAG = "CommentFragment";

    private View view;
    private TrackerActivity activity;
    private Button btCommentSave;
    private EditText etComment;

    public CommentFragment() {}

    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.tracker_activity_comment_fragment, container, false);

        this.etComment = (EditText)view.findViewById(R.id.comment_fragment_et_comment);
        this.btCommentSave = (Button)view.findViewById(R.id.comment_fragment_bt_save);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = (TrackerActivity) getActivity();

        btCommentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.attachmentDescription.put(activity.ATTACHMENT_DESCTIPTION_HEADER, etComment.getText());
                    activity.attachmentDescription.put(activity.ATTACHMENT_DESCTIPTION_CONTENT, etComment.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                activity.currentAttachmentObject.setDescription(activity.attachmentDescription.toString());
                activity.arrayAttachmentObject.add(activity.currentAttachmentObject);

                activity.changeFragment(activity.trackerFragment);
                Config.hideKeyboard(activity);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
