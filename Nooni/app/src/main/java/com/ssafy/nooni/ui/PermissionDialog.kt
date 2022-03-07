package com.ssafy.nooni.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.ssafy.nooni.R

class PermissionDialog(val context: Context) {

    lateinit var negativeText: String
    lateinit var positiveText: String
    lateinit var content: String
    lateinit var positiveClickListener: View.OnClickListener
    lateinit var negativeClickListener: View.OnClickListener
    private val dialog = Dialog(context).apply{
        setContentView(R.layout.dialog_permission)
        window?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.rounded_rectangle))
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    fun setContent(content: String): PermissionDialog {
        this.content = content
        return this
    }

    fun setNegativeButtonText(text: String): PermissionDialog {
        this.negativeText = text
        return this
    }

    fun setPositiveButtonText(text: String): PermissionDialog {
        this.positiveText = text
        return this
    }

    fun setOnPositiveClickListener(clickListener: View.OnClickListener): PermissionDialog {
        positiveClickListener = clickListener
        return this
    }

    fun setOnNegativeClickListener(clickListener: View.OnClickListener): PermissionDialog {
        negativeClickListener = clickListener
        return this
    }

    fun build(): Dialog {
        if(this::content.isInitialized) {
            dialog.findViewById<TextView>(R.id.tv_permissionDialog_content).text = this.content
        }
        if(this::negativeText.isInitialized) {
            dialog.findViewById<TextView>(R.id.tv_permissionDialog_neg).text = this.negativeText
        }
        if(this::positiveText.isInitialized) {
            dialog.findViewById<TextView>(R.id.tv_permissionDialog_pos).text = this.positiveText
        }
        if(this::positiveClickListener.isInitialized) {
            dialog.findViewById<TextView>(R.id.tv_permissionDialog_pos).setOnClickListener {
                positiveClickListener.onClick(it)
                dialog.dismiss()
            }
        } else {
            dialog.findViewById<TextView>(R.id.tv_permissionDialog_pos).setOnClickListener { dialog.dismiss() }
        }
        if(this::negativeClickListener.isInitialized) {
            dialog.findViewById<TextView>(R.id.tv_permissionDialog_neg).setOnClickListener{
                negativeClickListener.onClick(it)
                dialog.dismiss()
            }
        } else {
            dialog.findViewById<TextView>(R.id.tv_permissionDialog_neg).setOnClickListener { dialog.dismiss() }
        }
        return dialog
    }
}