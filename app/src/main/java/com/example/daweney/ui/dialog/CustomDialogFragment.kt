package com.example.daweney.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.daweney.R

import kotlinx.android.synthetic.main.dialog_error_layout.view.*


class CustomDialogFragment(private val msg:String, context: Context): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_error_layout, null)
        val builder = AlertDialog.Builder(activity)
        dialogView.message.text=msg
        builder.setView(dialogView)

        builder.setPositiveButton(context?.getText(R.string.ok)) { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.navy_blue_200)


        return dialog
    }
}
