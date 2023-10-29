package com.example.safehome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import com.example.safehome.activity.PersonalInfoActivity
import com.example.safehome.activity.SignInActivity
import com.example.safehome.activity.SignUpActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var displayMetrics: DisplayMetrics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        saveDisplayMetrics()

        Handler(Looper.getMainLooper()).postDelayed({

            if (Utils.getBooleanPref(this@SplashActivity, "IsLoggedIn", false)) {

                val  Mobile_Number = Utils.getStringPref(this, "Mobile_Number", "")
                Log.d("Mobile_Number", Mobile_Number.toString())

                val login = Intent(this, HomeActivity::class.java)
                startActivity(login)
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            } else {
                val login = Intent(this, SignInActivity::class.java)
                startActivity(login)
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

        }, 3000)
    }

    private fun saveDisplayMetrics() {
        // saving default screen width and height
        displayMetrics = DisplayMetrics()
        this@SplashActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        Utils.screenWidth = displayMetrics.widthPixels
        Utils.screenHeight = displayMetrics.heightPixels
    }

}