package com.jho3r.financeapp.network

import com.google.firebase.firestore.FirebaseFirestore
import com.jho3r.financeapp.models.User

private const val USERS_COLLECTION = "users"
private const val ACCOUNTS_COLLECTION = "accounts"

class FirestoreService(private val firebaseFirestore: FirebaseFirestore) {

    fun createUser(user: User, callback: Callback<Void>) {
        firebaseFirestore
            .collection(USERS_COLLECTION)
            .document(user.id)
            .set(user.getData())
            .addOnSuccessListener {
                val batch = firebaseFirestore.batch()
                user.getAccounts().forEach { (id, account) ->
                    val accountRef = firebaseFirestore
                        .collection(USERS_COLLECTION).document(user.id)
                        .collection(ACCOUNTS_COLLECTION).document(id)
                    batch.set(accountRef, account)
                }
                batch.commit()
                    .addOnSuccessListener { callback.onSuccess(null) }
                    .addOnFailureListener { callback.onFailure(it) }
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

}