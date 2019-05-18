package com.habittracker.activity.splashscreen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.habittracker.R
import com.habittracker.activity.home.HomeActivity
import com.habittracker.activity.registerbio.RegisterBioActivity
import com.habittracker.library.PreferenceHelper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (PreferenceHelper(this).userId == ""){
                startActivity(Intent(this, RegisterBioActivity::class.java))
                finish()
            }else {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }, 3000)

    }
}
