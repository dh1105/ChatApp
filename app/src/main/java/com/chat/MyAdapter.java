package com.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by user on 2/13/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<Message> messages;

    private static final int VIEW_HOLDER_TYPE_1=1;
    private static final int VIEW_HOLDER_TYPE_2=2;
    ActionMode actionMode;
    private SparseBooleanArray mSelectedItemsIds;

    public static class ViewHolder_Type1 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mymessageTextView, mytimeTextView;
        public ImageView myimage;
        RelativeLayout mymsg_layout;

        public ViewHolder_Type1(View v) {
            super(v);
            this.mymessageTextView = (TextView) v.findViewById(R.id.mymessageTextView);
            this.mytimeTextView = (TextView) v.findViewById(R.id.mytimeTextView);
            this.myimage = (ImageView) v.findViewById(R.id.image_sent);
            this.mymsg_layout = (RelativeLayout) v.findViewById(R.id.mymsg_layout);
        }
    }

    public static class ViewHolder_Type2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView messageTextView, timeTextView;
        public ImageView recimage;
        RelativeLayout msg_layout;

        public ViewHolder_Type2(View v) {
            super(v);
            this.messageTextView = (TextView) v.findViewById(R.id.messageTextView);
            this.timeTextView = (TextView) v.findViewById(R.id.timeTextView);
            this.recimage = (ImageView) v.findViewById(R.id.image_Rec);
            this.msg_layout = (RelativeLayout) v.findViewById(R.id.msg_layout);
        }
    }


    public MyAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        switch (viewType) {
            // create a new view

            case VIEW_HOLDER_TYPE_1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mymessage, parent, false);
                ViewHolder_Type1 vh1 = new ViewHolder_Type1(v);
                return vh1;

            case VIEW_HOLDER_TYPE_2:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false);
                ViewHolder_Type2 vh2 = new ViewHolder_Type2(v);
                return vh2;

            default:
                break;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)) {

            case VIEW_HOLDER_TYPE_1:
                final ViewHolder_Type1 viewholder1 = (ViewHolder_Type1) holder;
                TextView mytimeView = viewholder1.mytimeTextView;
                mytimeView.setText(messages.get(position).getTime());
                TextView mymsgView = viewholder1.mymessageTextView;
                ImageView myimageView = viewholder1.myimage;
                if(messages.get(position).getType().equals("image")){
                    myimageView.setVisibility(View.VISIBLE);
                    mymsgView.setVisibility(View.GONE);
                    Picasso.get().load(messages.get(position).getMessage()).placeholder(R.drawable.progress_animation).into(myimageView);
                    myimageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(getSelectedCount()==0) {
                                Intent in = new Intent(context, ViewImage.class);
                                in.putExtra("img", messages.get(position).getMessage());
                                context.startActivity(in);
                            }
                        }
                    });
                }
                else {
                    myimageView.setVisibility(View.GONE);
                    mymsgView.setVisibility(View.VISIBLE);
                    mymsgView.setClickable(true);
                    mymsgView.setMovementMethod(LinkMovementMethod.getInstance());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        mymsgView.setText(Html.fromHtml(messages.get(position).getMessage(), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        mymsgView.setText(Html.fromHtml(messages.get(position).getMessage()));
                    }
                    //mymsgView.setText(messages.get(position).getMessage());
                }
                RelativeLayout my_rel = viewholder1.mymsg_layout;
                my_rel.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
                        : Color.TRANSPARENT);
                break;

            case VIEW_HOLDER_TYPE_2:
                final ViewHolder_Type2 viewholder2 = (ViewHolder_Type2) holder;
                TextView timeView = viewholder2.timeTextView;
                timeView.setText(messages.get(position).getTime());
                TextView msgView = viewholder2.messageTextView;
                ImageView myimageView1 = viewholder2.recimage;
                if(messages.get(position).getType().equals("image")){
                    myimageView1.setVisibility(View.VISIBLE);
                    msgView.setVisibility(View.GONE);
                    Picasso.get().load(messages.get(position).getMessage()).placeholder(R.drawable.progress_animation).into(myimageView1);
                    myimageView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(getSelectedCount()==0) {
                                Intent in = new Intent(context, ViewImage.class);
                                in.putExtra("img", messages.get(position).getMessage());
                                context.startActivity(in);
                            }
                        }
                    });
                }
                else {
                    myimageView1.setVisibility(View.GONE);
                    msgView.setVisibility(View.VISIBLE);
                    msgView.setClickable(true);
                    msgView.setMovementMethod(LinkMovementMethod.getInstance());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        msgView.setText(Html.fromHtml(messages.get(position).getMessage(), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        msgView.setText(Html.fromHtml(messages.get(position).getMessage()));
                    }
                }
                //msgView.setText(messages.get(position).getMessage());
                RelativeLayout my_rel1 = viewholder2.msg_layout;
                my_rel1.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
                        : Color.TRANSPARENT);
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 1 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (messages.get(position).fromMe())
            return VIEW_HOLDER_TYPE_1;
        else
            return VIEW_HOLDER_TYPE_2;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
