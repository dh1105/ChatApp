package com.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class Toolbar_ActionMode_Callback implements ActionMode.Callback {

    private Context context;
    private MyAdapter recyclerView_adapter;
    private ArrayList<Message> message_models;
    private boolean isListViewFragment;
    public Toolbar_ActionMode_Callback(Context context, MyAdapter recyclerView_adapter, ArrayList<Message> message_models, boolean isListViewFragment) {
        this.context = context;
        this.recyclerView_adapter = recyclerView_adapter;
        this.message_models = message_models;
        this.isListViewFragment = isListViewFragment;
    }
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_mode, menu);         //Inflate the menu over action mode
        return true;
    }
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
//        if (Build.VERSION.SDK_INT < 11) {
//            MenuItemCompat.setShowAsAction(menu.findItem(R.id.copy), MenuItemCompat.SHOW_AS_ACTION_NEVER);
//        } else {
//            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(R.id.action_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(R.id.action_forward).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        }
        return true;
    }
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.copy:
                //Get selected ids on basis of current fragment action mode
                SparseBooleanArray selected;
                selected = recyclerView_adapter.getSelectedIds();
                int selectedMessageSize = selected.size();
                //Loop to all selected items
                ArrayList<String> select = new ArrayList<>();
                for (int i = (selectedMessageSize - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        //get selected data in Model
                        Message model = message_models.get(selected.keyAt(i));
                        select.add(model.getMessage());
                        //Print the data to show if its working properly or not
                        //Log.e("Selected Items", "Title - " + title + "\n");
                    }
                }
                StringBuilder listString = new StringBuilder();

                for (String s : select)
                {
                    listString.append(s).append("\t");
                }
                String text = listString.toString();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(clip);
                //Toast.makeText(context, "You selected Copy menu.", Toast.LENGTH_SHORT).show();//Show toast
                mode.finish();          //Finish action mode
                Toast.makeText(context, "Text copied", Toast.LENGTH_LONG).show();
                break;

        }
        return false;
    }
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        recyclerView_adapter.removeSelection();         // remove selection
        Chat.setNullToActionMode();       //Set action mode null
    }
}
