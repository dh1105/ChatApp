package com.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    public static class ViewHolder_Type1 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mymessageTextView, mytimeTextView;
        public ImageView myimage;
        public ProgressBar prog;

        public ViewHolder_Type1(View v) {
            super(v);
            this.mymessageTextView = (TextView) v.findViewById(R.id.mymessageTextView);
            this.mytimeTextView = (TextView) v.findViewById(R.id.mytimeTextView);
            this.myimage = (ImageView) v.findViewById(R.id.image_sent);
            this.prog = (ProgressBar) v.findViewById(R.id.prog_rec2);
        }
    }

    public static class ViewHolder_Type2 extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView messageTextView, timeTextView;
        public ImageView recimage;
        public ProgressBar prog1;

        public ViewHolder_Type2(View v) {
            super(v);
            this.messageTextView = (TextView) v.findViewById(R.id.messageTextView);
            this.timeTextView = (TextView) v.findViewById(R.id.timeTextView);
            this.recimage = (ImageView) v.findViewById(R.id.image_Rec);
            this.prog1 = (ProgressBar) v.findViewById(R.id.prog_rec);
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
                ImageView myimageView = viewholder1.myimage;
                ProgressBar myprog = viewholder1.prog;
                if(messages.get(position).getType().equals("image")){
                    //myimageView.setVisibility(View.VISIBLE);
                    myprog.setVisibility(View.VISIBLE);
                    mymsgView.setVisibility(View.GONE);
                    Picasso.get().load(messages.get(position).getMessage()).into(myimageView);
                    myprog.setVisibility(View.GONE);
                    myimageView.setVisibility(View.VISIBLE);
                }
                else {
                    myimageView.setVisibility(View.GONE);
                    myprog.setVisibility(View.GONE);
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
                break;

            case VIEW_HOLDER_TYPE_2:
                ViewHolder_Type2 viewholder2 = (ViewHolder_Type2) holder;
                TextView timeView = viewholder2.timeTextView;
                timeView.setText(messages.get(position).getTime());
                TextView msgView = viewholder2.messageTextView;
                ImageView myimageView1 = viewholder2.recimage;
                ProgressBar prog1 = viewholder2.prog1;
                if(messages.get(position).getType().equals("image")){
                    prog1.setVisibility(View.VISIBLE);
                    msgView.setVisibility(View.GONE);
                    Picasso.get().load(messages.get(position).getMessage()).into(myimageView1);
                    prog1.setVisibility(View.GONE);
                    myimageView1.setVisibility(View.VISIBLE);
                }
                else {
                    prog1.setVisibility(View.GONE);
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
