/*
 * Copyright 2018 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.med_manager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/** Manages the app's splash screen */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Animate the logo entry
        animation();

        //Waiting for 2 secs before launching landing page
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, LandingActivity.class));
                finish();
            }
        }, 2000);
    }

    /** Animation method. Animates entry from bottom to center*/
    private void animation() {
        try {
            ImageView imageView = (ImageView) findViewById(R.id.splash_screen_logo);
            imageView.setAlpha(1.0F);
            Animation localAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
            imageView.startAnimation(localAnimation);
        } catch (Exception e) {
        }
    }
}
