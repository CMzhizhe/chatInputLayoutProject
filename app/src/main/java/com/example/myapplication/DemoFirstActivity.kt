package com.example.myapplication

import android.os.Bundle

class DemoFirstActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun layoutId(): Int {
        return R.layout.activity_demo_first;
    }

}