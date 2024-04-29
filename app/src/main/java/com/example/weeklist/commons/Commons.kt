package com.example.weeklist.commons

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.example.weeklist.R

object Commons {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun loadingDialog(context: Context) :Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        return dialog
    }

}