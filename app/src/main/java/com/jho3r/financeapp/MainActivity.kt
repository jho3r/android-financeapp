package com.jho3r.financeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.ui.activity.ConfigActivity
import com.jho3r.financeapp.ui.activity.LoginActivity
import com.jho3r.financeapp.ui.activity.NewTransactionActivity
import com.jho3r.financeapp.ui.activity.SignupActivity

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var btnMainLogin: Button
    private lateinit var btnMainSignup: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnMainLogin = findViewById(R.id.btnMainLogin)
        btnMainLogin.setOnClickListener(this)

        btnMainSignup = findViewById(R.id.btnMainSignup)
        btnMainSignup.setOnClickListener(this)

        auth = Firebase.auth
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnMainLogin -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.btnMainSignup -> {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }
        }
    }


}