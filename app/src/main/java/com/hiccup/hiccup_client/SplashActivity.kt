package com.hiccup.hiccup_client

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val currentUser = Firebase.auth.currentUser
        val intent = Intent(
            this, if (currentUser == null) {
                LoginActivity::class.java
            } else {
                HomeActivity::class.java
            }
        )
        startActivity(intent)
        finish()
    }
}