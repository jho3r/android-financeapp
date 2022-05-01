package com.jho3r.financeapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.R
import com.jho3r.financeapp.network.AuthService
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService
import com.jho3r.financeapp.utils.UserMessagesHandler

private const val TAG = "MyApp.Login"
private const val USERID_KEY = "userId"

class LoginActivity : AppCompatActivity(), OnClickListener {

    private lateinit var btnLoginLogin: Button
    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText

    private lateinit var authService: AuthService
    private lateinit var firestoreService: FirestoreService
    private lateinit var messagesHandler: UserMessagesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.title = getString(R.string.login_title)

        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)

        btnLoginLogin = findViewById(R.id.btnLoginLogin)
        btnLoginLogin.setOnClickListener(this)

        authService = AuthService(Firebase.auth)
        firestoreService = FirestoreService(Firebase.firestore)
        messagesHandler = UserMessagesHandler(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnLoginLogin -> tryLogin(p0)
        }
    }

    private fun tryLogin(view: View) {
        view.isEnabled = false

        val email = etLoginEmail.text.toString()
        val password = etLoginPassword.text.toString()

        authService.login(
            email = email,
            password = password,
            callback = object : Callback<FirebaseUser>{
                override fun onSuccess(response: FirebaseUser?) {
                    if (response != null) {
                        val userid = response.uid
                        Log.d(TAG, "User logged in: $userid")
                        startDashboardActivity(userid)
                    }
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error logging in", exception)
                    messagesHandler
                        .showSnackbarErrorMessage(view, exception.message ?: "Error conectando con el servidor")
                }

            }
        )
    }

    private fun startDashboardActivity(userid: String) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra(USERID_KEY, userid)
        startActivity(intent)
        finishAffinity()
    }



}