package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.myapplication.utils.ActivityManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityManager.getInstance().addActivity(this)

        val buttonFirst = this.findViewById<Button>(R.id.first_main_one);
        val buttonSecond =  this.findViewById<Button>(R.id.first_main_two);

        buttonFirst.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

            }
        })

        buttonSecond.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

            }
        })
    }



    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().removeActivity(this)
    }

}