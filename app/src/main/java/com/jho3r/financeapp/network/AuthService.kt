package com.jho3r.financeapp.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthService (private val firebaseAuth: FirebaseAuth) {

    fun login(email: String, password: String, callback: Callback<FirebaseUser>) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccess(firebaseAuth.currentUser!!)
                } else {
                    callback.onFailure(task.exception!!)
                }
            }
    }

    fun register(email: String, password: String, callback: Callback<FirebaseUser>) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccess(firebaseAuth.currentUser!!)
                } else {
                    callback.onFailure(task.exception!!)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}