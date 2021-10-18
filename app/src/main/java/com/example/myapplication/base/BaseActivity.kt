package com.example.myapplication.base

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.SourcesAdapter
import com.example.myapplication.datasource.ListDataSources
import com.example.myapplication.utils.ActivityManager
import com.example.myapplication.utils.KeyBordUtils
import com.example.myapplication.weiget.ConversationInputPanel
import com.example.myapplication.weiget.InputAwareLayout
import com.example.myapplication.weiget.KeyboardAwareLinearLayout

/**
 * @date 创建时间:2021/10/16
 * @auther gaoxiaoxiong
 * @description base类
 */
public abstract class BaseActivity : AppCompatActivity(), ConversationInputPanel.OnConversationInputPanelStateChangeListener,
    KeyboardAwareLinearLayout.OnKeyboardShownListener {
    lateinit var inputAwareLayout: InputAwareLayout
    lateinit var recyclerView: RecyclerView
    lateinit var conversationInputPanel: ConversationInputPanel
    lateinit var sourcesAdapter: SourcesAdapter
    val listData = ListDataSources().list;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.getInstance().addActivity(this)
        setContentView(layoutId())
        inputAwareLayout = this.findViewById<InputAwareLayout>(R.id.chat_detail_input_aware_layout);
        recyclerView = this.findViewById<RecyclerView>(R.id.recyclerview_chat_detail)
        conversationInputPanel = this.findViewById<ConversationInputPanel>(R.id.chat_detail_conversaton_input_panel)
        sourcesAdapter = SourcesAdapter(listData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = sourcesAdapter;
        conversationInputPanel.init(this, inputAwareLayout)
        conversationInputPanel.setOnConversationInputPanelStateChangeListener(this)
        inputAwareLayout.addOnKeyboardShownListener(this)
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                conversationInputPanel.closeConversationInputPanel()
                return false;
            }
        })
        //滚动到最后一行
        recyclerView.post(Runnable {
            recyclerView.scrollToPosition(listData.size - 1)
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                conversationInputPanel.closeConversationInputPanel()
                val view = currentFocus
                if (view != null && view is EditText) {
                    KeyBordUtils.getInstance().clickEmptyHideEditTextKeyboard(ev, view)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    abstract fun layoutId(): Int

    /**
     * @date 创建时间:2021/10/17
     * @auther gaoxiaoxiong
     * @description 功能区关闭
     */
    override fun onInputPanelCollapsed() {

    }

    /**
     * @date 创建时间:2021/10/17
     * @auther gaoxiaoxiong
     * @description 功能区展开
     */
    override fun onInputPanelExpanded() {
        recyclerView.scrollToPosition(listData.size - 1)
    }

    /**
    * @date 创建时间:2021/10/17
    * @auther gaoxiaoxiong
    * @description 软键盘弹出
    */
    override fun onKeyboardShown() {
        recyclerView.post(object : Runnable{
            override fun run() {
                recyclerView.scrollToPosition(listData.size - 1)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.getInstance().removeActivity(this)
    }
}