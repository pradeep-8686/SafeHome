package com.example.safehome.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun replaceFragment(containerId: Int, fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, fragment).addToBackStack(null)
            .commit()


    }

}