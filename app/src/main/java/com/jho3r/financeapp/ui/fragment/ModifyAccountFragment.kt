package com.jho3r.financeapp.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.jho3r.financeapp.R
import com.jho3r.financeapp.models.Account

class ModifyAccountFragment(private val account:Account): DialogFragment() {



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_modify_account, null)
            builder.setView(view)
                .setPositiveButton("START",
                    DialogInterface.OnClickListener { dialog, id ->
                        // START THE GAME!
                    })
                .setNegativeButton("CANCEL",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    });
            val tvTitle : TextView = view.findViewById(R.id.tvFragAcctTitle)
            tvTitle.text = account.getName()
            val etBalance : EditText = view.findViewById(R.id.etFragAcctBalance)
            etBalance.setText(account.getBalance())
            val etDescr : EditText = view.findViewById(R.id.etFragAcctDescr)
            etDescr.setText(account.getDescription())
            val cbCash : CheckBox = view.findViewById(R.id.cbFragAcctCash)
            cbCash.isChecked = account.isCash()
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}