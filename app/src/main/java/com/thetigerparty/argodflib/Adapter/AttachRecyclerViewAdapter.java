package com.thetigerparty.argodflib.Adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by louis on 03/11/2017.
 */

public class AttachRecyclerViewAdapter extends RecyclerView.Adapter<AttachRecyclerViewAdapter.AttachmentViewHolder> {
    private final static String TAG = AttachRecyclerViewAdapter.class.getSimpleName();

    private OnAttachClickListener listener;
    private List<AttachmentObject> attachmentObjectList = new ArrayList<>();

    @Override
    public AttachmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attachment, parent, false);
        return new AttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AttachmentViewHolder holder, int position) {
        final AttachmentObject attachmentObject = attachmentObjectList.get(holder.getAdapterPosition());

        if (!attachmentObject.getPath().equals("")) {
            Glide.with(holder.ivAttachment.getContext())
                    .load(new File(attachmentObject.getPath()))
                    .thumbnail(0.2f)
                    .into(holder.ivAttachment);
        }

        try {
            JSONObject jsonObject = new JSONObject(attachmentObject.getDescription());
            holder.tvHeader.setText(jsonObject.getString(AttachmentObject.KEY_HEADER));
            holder.tvDescription.setText(jsonObject.getString(AttachmentObject.KEY_CONTENT));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder: ", e);
        }

        if (listener != null) {
            holder.iBtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEditClick(holder.getAdapterPosition(), attachmentObject);
                }
            });

            holder.iBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteClick(holder.getAdapterPosition(), attachmentObject);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return attachmentObjectList.size();
    }

    public void addOnAttachClickListener(OnAttachClickListener listener) {
        this.listener = listener;
    }

    public void addItem(AttachmentObject attachmentObject) {
        attachmentObjectList.add(attachmentObject);
        notifyItemInserted(attachmentObjectList.size() - 1);

        Log.d(TAG, "addItem: total: " + String.valueOf(attachmentObjectList.size()));
    }

    public void replaceItem(int position, AttachmentObject attachmentObject) {
        attachmentObjectList.set(position, attachmentObject);
        notifyItemChanged(position);

        Log.d(TAG, "replaceItem: total: " + String.valueOf(attachmentObjectList.size()));
    }

    public void removeItem(int position) {
        attachmentObjectList.remove(position);
        notifyItemRemoved(position);

        Log.d(TAG, "removeItem: total: " + String.valueOf(attachmentObjectList.size()));
    }

    public List<AttachmentObject> getAttachmentObjectList() {
        return attachmentObjectList;
    }

    public static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAttachment;
        private TextView tvHeader;
        private TextView tvDescription;
        private ImageButton iBtnEdit;
        private ImageButton iBtnDelete;

        public AttachmentViewHolder(View itemView) {
            super(itemView);

            ivAttachment = (ImageView) itemView.findViewById(R.id.iv_attachment);
            tvHeader = (TextView) itemView.findViewById(R.id.tv_header);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            iBtnEdit = (ImageButton) itemView.findViewById(R.id.iBtn_edit);
            iBtnDelete = (ImageButton) itemView.findViewById(R.id.iBtn_delete);

            tvHeader.setTypeface(null, Typeface.BOLD_ITALIC);
        }
    }

    public interface OnAttachClickListener {
        void onEditClick(int position, AttachmentObject object);
        void onDeleteClick(int position, AttachmentObject object);
    }
}
