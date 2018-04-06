package com.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2/13/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<Message> messages;

    private static final int VIEW_HOLDER_TYPE_1=1;
    private static final int VIEW_HOLDER_TYPE_2=2;

    public static class ViewHolder_Type1 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mymessageTextView, mytimeTextView;

        public ViewHolder_Type1(View v) {
            super(v);
            this.mymessageTextView = (TextView) v.findViewById(R.id.mymessageTextView);
            this.mytimeTextView = (TextView) v.findViewById(R.id.mytimeTextView);
        }
    }

    public static class ViewHolder_Type2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView messageTextView, timeTextView;
        public ViewHolder_Type2(View v) {
            super(v);
            this.messageTextView = (TextView) v.findViewById(R.id.messageTextView);
            this.timeTextView = (TextView) v.findViewById(R.id.timeTextView);
        }
    }


    public MyAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {

            case VIEW_HOLDER_TYPE_1:
                ViewHolder_Type1 viewholder1 = (ViewHolder_Type1) holder;
                TextView mytimeView = viewholder1.mytimeTextView;
                mytimeView.setText(messages.get(position).getTime());
                TextView mymsgView = viewholder1.mymessageTextView;
                mymsgView.setText(messages.get(position).getMessage());
                break;

            case VIEW_HOLDER_TYPE_2:
                ViewHolder_Type2 viewholder2 = (ViewHolder_Type2) holder;
                TextView timeView = viewholder2.timeTextView;
                timeView.setText(messages.get(position).getTime());
                TextView msgView = viewholder2.messageTextView;
                msgView.setText(messages.get(position).getMessage());
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

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
