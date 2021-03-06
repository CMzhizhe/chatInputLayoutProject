package com.example.myapplication

import android.content.Intent
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
                val intent = Intent(this@MainActivity,DemoFirstActivity::class.java)
                startActivity(intent)
            }
        })

        buttonSecond.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(this@MainActivity,DemoSecondActivity::class.java)
                startActivity(intent)
            }
        })
    }



    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().removeActivity(this)
    }

}