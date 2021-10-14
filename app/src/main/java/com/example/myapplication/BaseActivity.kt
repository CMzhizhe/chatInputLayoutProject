package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.SourcesAdapter
import com.example.myapplication.datasource.ListDataSources
import com.example.myapplication.utils.ActivityManager
import com.example.myapplication.weiget.ConversationInputPanel
import com.example.myapplication.weiget.InputAwareLayout

public abstract class  BaseActivity : AppCompatActivity() {
    lateinit var backImage:ImageView;
    lateinit var inputAwareLayout: InputAwareLayout
    lateinit var recyclerView: RecyclerView
    lateinit var conversationInputPanel: ConversationInputPanel
    lateinit var sourcesAdapter: SourcesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.getInstance().addActivity(this)
        setContentView(layoutId())
        backImage = this.findViewById<ImageView>(R.id.iv_backimage);
        inputAwareLayout =  this.findViewById<InputAwareLayout>(R.id.chat_detail_input_aware_layout);
        recyclerView = this.findViewById<RecyclerView>(R.id.recyclerview_chat_detail)
        conversationInputPanel = this.findViewById<ConversationInputPanel>(R.id.chat_detail_conversaton_input_panel)
        sourcesAdapter = SourcesAdapter(ListDataSources().list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = sourcesAdapter;
    }

    abstract fun layoutId():Int

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().removeActivity(this)
    }
}