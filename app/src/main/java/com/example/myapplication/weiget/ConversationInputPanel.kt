package com.example.myapplication.weiget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.example.myapplication.R

public class ConversationInputPanel  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs,
    defStyleAttr), View.OnClickListener {
    private var emotionHeight = 0;
    private var functionHeight = 0;

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConversationInputPanel, defStyleAttr, 0);
        emotionHeight = typedArray.getDimensionPixelOffset(R.styleable.ConversationInputPanel_conversation_emoji_height, context.getResources().getDimensionPixelOffset(R.dimen.dp220))
        functionHeight = typedArray.getDimensionPixelOffset(R.styleable.ConversationInputPanel_conversation_function_height, context.resources.getDimensionPixelOffset(R.dimen.dp175))
        typedArray.recycle()
    }

    override fun onClick(v: View?) {

    }
}