package com.zx.lunbanpress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class PlusImgActivity extends AppCompatActivity {
    
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_img);

        String path = getIntent().getStringExtra("path");
        imageView = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(path).into(imageView);
    }
}
