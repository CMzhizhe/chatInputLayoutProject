package com.example.myapplication.datasource

import com.example.myapplication.bean.User

public class ListDataSources {
    val list:MutableList<User> = mutableListOf();

    init {
        for (i in 0..100){
            val user = User();
            user.userAge = i;
            user.userName = "张三" + i
            list.add(user)
        }
    }

}