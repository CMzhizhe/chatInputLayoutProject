package com.example.myapplication.weiget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

public class ConversationInputPanel  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs,
    defStyleAttr), View.OnClickListener {
    private var emotionHeight = 0;
    private var functionHeight = 0;

    private var rootLinearLayout: InputAwareLayout? = null;
    private var inputPanelView: View? = null;
    private val etInputText: EditText by lazy { inputPanelView!!.findViewById<EditText>(R.id.et_chat_detail_input) }
    private val emojiKeyBordHeightFrameLayout by lazy { inputPanelView!!.findViewById<KeyboardHeightFrameLayout>(R.id.keybord_height_conversation_input_panel_emoji) }
    private val functionKeyBordHeightFrameLayout by lazy { inputPanelView!!.findViewById<KeyboardHeightFrameLayout>(R.id.keybord_height_conversation_input_panel_function) }
    private var onConversationInputPanelStateChangeListener: OnConversationInputPanelStateChangeListener? = null
    private val btEmoji by lazy { inputPanelView!!.findViewById<Button>(R.id.bt_chat_detail_emoticon) }
    private val btFunction by lazy { inputPanelView!!.findViewById<Button>(R.id.bt_chat_detail_ext) }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConversationInputPanel, defStyleAttr, 0);
        emotionHeight = typedArray.getDimensionPixelOffset(R.styleable.ConversationInputPanel_conversation_emoji_height, context.getResources().getDimensionPixelOffset(R.dimen.dp220))
        functionHeight = typedArray.getDimensionPixelOffset(R.styleable.ConversationInputPanel_conversation_function_height, context.resources.getDimensionPixelOffset(R.dimen.dp175))
        typedArray.recycle()
    }

    /**
    * @date 创建时间:2021/10/16
    * @auther gaoxiaoxiong
    * @description 初始化
    */
    fun init(activity:AppCompatActivity,rootInputAwareLayout: InputAwareLayout){
        inputPanelView = LayoutInflater.from(activity).inflate(R.layout.view_conversation_input_panel, this, true);
        this.rootLinearLayout = rootInputAwareLayout
        btEmoji.setOnClickListener(this)
        btFunction.setOnClickListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.bt_chat_detail_emoticon->{//按钮1
                if (rootLinearLayout!!.currentInput === emojiKeyBordHeightFrameLayout) {
                    hideEmotionLayout()
                    rootLinearLayout!!.showSoftkey(etInputText)
                } else {
                    showEmotionLayout()
                }
            }

            R.id.bt_chat_detail_ext->{//按钮2
                if (rootLinearLayout!!.currentInput === functionKeyBordHeightFrameLayout) {
                    hideConversationExtension()
                    rootLinearLayout!!.showSoftkey(etInputText)
                } else {
                    showConversationExtension()
                }
            }
        }
    }


    private fun showConversationExtension() {
        rootLinearLayout!!.show(etInputText, functionKeyBordHeightFrameLayout, functionHeight)
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener!!.onInputPanelExpanded()
        }
    }

    private fun hideConversationExtension() {
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener!!.onInputPanelCollapsed()
        }
    }

    private fun showEmotionLayout() {
        rootLinearLayout!!.show(etInputText, emojiKeyBordHeightFrameLayout, emotionHeight)
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener!!.onInputPanelExpanded()
        }
    }

    private fun hideEmotionLayout() {
        if (onConversationInputPanelStateChangeListener != null) {
            onConversationInputPanelStateChangeListener!!.onInputPanelCollapsed()
        }
    }

    fun setOnConversationInputPanelStateChangeListener(onConversationInputPanelStateChangeListener: OnConversationInputPanelStateChangeListener?) {
        this.onConversationInputPanelStateChangeListener = onConversationInputPanelStateChangeListener
    }


    fun closeConversationInputPanel() {
        rootLinearLayout!!.hideAttachedInput(true)
        rootLinearLayout!!.hideCurrentInput(etInputText)
    }

    interface OnConversationInputPanelStateChangeListener {
        /**
         * 输入面板展开
         */
        fun onInputPanelExpanded()

        /**
         * 输入面板关闭
         */
        fun onInputPanelCollapsed()
    }
}