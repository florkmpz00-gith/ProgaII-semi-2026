package com.example.auramoda;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.auramoda.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Animation floatAnim = AnimationUtils.loadAnimation(this, R.anim.float_logo);
        ivLogo.startAnimation(floatAnim);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 5000);
    }
}