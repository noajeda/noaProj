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
    ImageView imgSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgSplash = findViewById(R.id.imgSplash);

        Thread mSplashThread = new Thread(){
            @Override
            public void run(){
                try{
                    synchronized (this){
                        Animation myFadeInAnimation = AnimationUtils.loadAnimation(Splash.this, R.anim.tween); // טעינת אנימציה
                        imgSplash.startAnimation(myFadeInAnimation); // הפעלת האנימציה על התמונה
                        wait(3000);
                    }
                }
                catch (InterruptedException ex){
                }
                finish(); // סגירת הSplash

                Intent goMainActivity = new Intent(Splash.this, MainActivity.class);
                startActivity(goMainActivity); // מעבר למסך הראשי
            }
        };
        mSplashThread.start(); // הפעלת הThread
    }
}