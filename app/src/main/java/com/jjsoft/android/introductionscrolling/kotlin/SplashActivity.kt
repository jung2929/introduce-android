package com.jjsoft.android.introductionscrolling.kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.jjsoft.android.introductionscrolling.R
import com.jjsoft.android.introductionscrolling.kotlin.data.SCREEN_DELAY_MILLISECONDS

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(Intent(applicationContext, ScrollingActivity::class.java))
            finish()
        }, SCREEN_DELAY_MILLISECONDS.toLong())
    }
}