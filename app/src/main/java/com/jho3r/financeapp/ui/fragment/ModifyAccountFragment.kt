package com.jho3r.financeapp.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.R
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.network.FirestoreService

class ModifyAccountFragment(private val account:Account, private val onPositive: (Account) -> Unit)
    : DialogFragment() {

    private lateinit var etBalance: EditText
    private lateinit var etDescr: EditText
    private lateinit var cbCash: CheckBox

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_modify_account, null)

            val tvTitle : TextView = view.findViewById(R.id.tvFragAcctTitle)
            tvTitle.text = account.getName()

            etBalance = view.findViewById(R.id.etFragAcctBalance)
            etDescr = view.findViewById(R.id.etFragAcctDescr)
            cbCash = view.findViewById(R.id.cbFragAcctCash)

            etBalance.setText(account.getBalance())
            etDescr.setText(account.getDescription())
            cbCash.isChecked = account.isCash()

            builder.setView(view)
                .setPositiveButton("SAVE",
                    DialogInterface.OnClickListener { dialog, id ->
                        account.setBalance(etBalance.text.toString())
                        account.setDescription(etDescr.text.toString())
                        account.setCash(cbCash.isChecked)
                        onPositive(account)
                    })
                .setNegativeButton("CANCEL",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}