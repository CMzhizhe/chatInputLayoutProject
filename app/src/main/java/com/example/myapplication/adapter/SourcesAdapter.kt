package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.bean.User

class SourcesAdapter(val list : MutableList<User>) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>(),View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_item,parent,false);
        itemView.setOnClickListener(this)
        return ViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTextview.setText(list.get(position).userName + "," + list.get(position).userAge)
    }

    override fun getItemCount(): Int {
       return list.size;
    }

    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val tvTextview = view.findViewById<TextView>(R.id.tv_view_item_content);
    }

    override fun onClick(v: View?) {

    }
}