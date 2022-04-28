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
import com.jho3r.financeapp.models.User
import com.jho3r.financeapp.network.AuthService
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService

private const val USERID_KEY = "userId"

class SignupActivity : AppCompatActivity(), OnClickListener {

    private val tag = "MyApp.Signup"

    private lateinit var btnSignup: Button
    private lateinit var etSignupEmail: EditText
    private lateinit var etSignupPassword: EditText
    private lateinit var etSignupUsername: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var authService: AuthService
    private lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etSignupEmail = findViewById(R.id.etSignupEmail)
        etSignupPassword = findViewById(R.id.etSignupPassword)
        etSignupUsername = findViewById(R.id.etSignupUsername)

        btnSignup = findViewById(R.id.btnSignup)
        btnSignup.setOnClickListener(this)

        auth = Firebase.auth
        authService = AuthService(auth)
        firestoreService = FirestoreService(Firebase.firestore)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnSignup -> {
                trySignup(p0)
            }
        }
    }

    private fun trySignup(view: View) {
        view.isEnabled = false

        val email = etSignupEmail.text.toString()
        val password = etSignupPassword.text.toString()
        val username = etSignupUsername.text.toString()
        authService.register(
            email = email,
            password = password,
            callback = object : Callback<FirebaseUser>{
                override fun onSuccess(response: FirebaseUser?) {
                    Log.d(tag, "Successfully registered user ${response?.uid}")
                    if (response != null) {
                        val user = User(
                            id = response.uid,
                            username = username,
                        )
                        firestoreService.createUser(user, callback = object : Callback<Void>{
                            override fun onSuccess(response: Void?) {
                                Log.d(tag, "Successfully created user ${user.id}")
                                startDashboardActivity(user.id)
                            }

                            override fun onFailure(error: Exception) {
                                Log.e(tag, "Error creating user ${user.id} ${error.message}")
                                showErrorMessage(view, error.message ?: "Error creating user")
                            }
                        })
                    }
                }

                override fun onFailure(exception: Exception) {
                    Log.e(tag, "Error: ${exception.message}")
                    showErrorMessage(view, exception.message ?: "Error conectando con el servidor")
                }
            }
        )
    }

    private fun startDashboardActivity(userId: String) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra(USERID_KEY, userId)
        startActivity(intent)
    }

    private fun showErrorMessage(view: View, message: String) {
        view.isEnabled = true
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}