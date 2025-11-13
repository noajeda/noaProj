package com.example.noaproj;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        img = findViewById(R.id.img);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Thread mSplashTheard = new Thread(){
            @Override
            public void run(){
                try{
                    synchronized (this){
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(Splash.this, R.anim.tween);
                        img.startAnimation(myFadeInAnimation);
                        wait(3000);
                    }
                }
                catch (InterruptedException ex){
                }
                finish();

                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            }
        };
        mSplashTheard.start();
    }
}