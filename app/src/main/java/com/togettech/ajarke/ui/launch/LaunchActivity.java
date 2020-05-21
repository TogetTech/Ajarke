package com.togettech.ajarke.ui.launch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.togettech.ajarke.MainActivity;
import com.togettech.ajarke.R;
import com.togettech.ajarke.ui.login.LoginActivity;

public class LaunchActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.transition);
        LinearLayout img_logo = (LinearLayout) findViewById(R.id.img_logo);
        TextView powered= (TextView) findViewById(R.id.powered_by_togettech_inc);
        img_logo.startAnimation(animation);
        powered.startAnimation(animation);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 4000);
    }
}
