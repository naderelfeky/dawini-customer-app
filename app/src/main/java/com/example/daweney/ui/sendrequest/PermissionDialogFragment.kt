package com.example.daweney.ui.sendrequest

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.example.daweney.R
import kotlinx.android.synthetic.main.dialog_error_layout.view.header_title
import kotlinx.android.synthetic.main.dialog_error_layout.view.message

class PermissionDialogFragment(private val msg:String,private val askPermissionInterface: AskPermissionInterface): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_error_layout, null)
        val builder = AlertDialog.Builder(activity)
        dialogView.header_title.text= context?.getText(R.string.permission_required)
        dialogView.message.text=msg
        builder.setView(dialogView)

        builder.setPositiveButton(context?.getText(R.string.granted_permission)) { dialog, which ->
            askPermissionInterface.askLocationPermissionInterface()
        }
        builder.setNegativeButton(context?.getText(R.string.cancel)){dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.navy_blue_400)

        return dialog
    }

}