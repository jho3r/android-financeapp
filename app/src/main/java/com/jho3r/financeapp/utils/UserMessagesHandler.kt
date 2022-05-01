package com.jho3r.financeapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class UserMessagesHandler(private val context: Context) {
    fun showSnackbarErrorMessage(view: View, message: String) {
        view.isEnabled = true
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun showToastErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showSnackbarSuccessMessage(view: View, message: String) {
        view.isEnabled = true
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun showToastSuccessMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}