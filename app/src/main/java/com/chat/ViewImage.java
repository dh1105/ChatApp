package com.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Intent in = getIntent();
        String image = in.getStringExtra("img");
        ZoomageView zoomageView = (ZoomageView) findViewById(R.id.myZoomageView);
        Picasso.get().load(image).placeholder(R.drawable.progress_animation).into(zoomageView);
    }
}
