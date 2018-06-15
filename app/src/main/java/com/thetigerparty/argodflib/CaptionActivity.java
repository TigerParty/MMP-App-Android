package com.thetigerparty.argodflib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thetigerparty.argodflib.HelperClass.ImageProcess;
import com.thetigerparty.argodflib.Object.AttachmentObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by fredtsao on 10/27/16.
 */

public class CaptionActivity extends Activity {
    private static final String TAG = CaptionActivity.class.getSimpleName();

    public static final String KEY_ATTACHMENT = "attachment";
    public static final String KEY_POSITION = "position";

    private ImageView ivPicture;

    private EditText caption_et_header;
    private EditText caption_et_description;

    private Button caption_bt_save;
    private Button caption_bt_cancel;
    private Button bt_header_back;

    private TextView tv_header_title;

    private String attachmentHeader = "";
    private String attachmentDescription = "";

    private AttachmentObject attachmentObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caption_activity);

        if(getIntent().getExtras() == null) {
            failed_return();
        }

        attachmentObject = (AttachmentObject) getIntent().getSerializableExtra(KEY_ATTACHMENT);
        try {
            JSONObject descriptionObject = new JSONObject(attachmentObject.getDescription());
            attachmentHeader = descriptionObject.getString(AttachmentObject.KEY_HEADER);
            attachmentDescription = descriptionObject.getString(AttachmentObject.KEY_CONTENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        find_view();
        load_image();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Runtime.getRuntime().gc();
    }

    private void find_view() {
        ivPicture = (ImageView) findViewById(R.id.iv_picture);

        caption_et_header = (EditText) findViewById(R.id.caption_edit_header);
        caption_et_header.setText(attachmentHeader);

        caption_et_description = (EditText) findViewById(R.id.caption_edit);
        caption_et_description.setText(attachmentDescription);

        caption_bt_save = (Button) findViewById(R.id.caption_bt_save);
        caption_bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(AttachmentObject.KEY_HEADER, caption_et_header.getText().toString());
                    jsonObject.put(AttachmentObject.KEY_CONTENT, caption_et_description.getText().toString());
                    attachmentObject.setDescription(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();

                    failed_return();
                }

                File file = new File(attachmentObject.getPath());
                if (file.exists()) {
                    attachmentObject.setName(file.getName());
                    attachmentObject.setType(ImageProcess.getMimeTypeByPath(attachmentObject.getPath()));
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra(KEY_ATTACHMENT, attachmentObject);
                resultIntent.putExtra(KEY_POSITION, getIntent().getIntExtra(KEY_POSITION, 0));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        caption_bt_cancel = (Button) findViewById(R.id.caption_bt_cancel);
        caption_bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failed_return();
            }
        });

        tv_header_title = (TextView)findViewById(R.id.tv_header_title);
        tv_header_title.setText("Report");

        bt_header_back = (Button) findViewById(R.id.bt_header_back);
        bt_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failed_return();
            }
        });
    }

    private void load_image(){
        File file = new File(attachmentObject.getPath());
        if(!file.exists()){
            Log.e(TAG, "load_image: File does not exist");
            failed_return();
        }

        Glide.with(this)
                .load(file)
                .thumbnail(0.2f)
                .into(ivPicture);
    }

    private void failed_return(){
        setResult(RESULT_CANCELED);
        finish();
    }
}
