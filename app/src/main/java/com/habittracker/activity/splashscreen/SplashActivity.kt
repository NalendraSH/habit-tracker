package com.habittracker.activity.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.habittracker.R
import com.habittracker.activity.home.HomeActivity
import com.habittracker.activity.login.LoginActivity
import com.habittracker.activity.registerbio.RegisterBioActivity
import com.habittracker.library.PreferenceHelper

class SplashActivity : AppCompatActivity() {

    private val userId: String by lazy { PreferenceHelper(this).userId!! }
    private val userName: String by lazy { PreferenceHelper(this).userName!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (userName == "") {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }else {
                if (userId == "") {
                    startActivity(Intent(this@SplashActivity, RegisterBioActivity::class.java))
                }else {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                }
            }
            finish()
        }, 3000)

    }
}
