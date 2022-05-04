package com.jho3r.financeapp.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.models.Transaction
import com.jho3r.financeapp.models.User
import com.jho3r.financeapp.utils.Constants

private const val USERS_COLLECTION = "users"
private const val ACCOUNTS_COLLECTION = "accounts"
private const val TRANSACTIONS_COLLECTION = "transactions"
private const val HIDDEN_TRANSACTIONS_COLLECTION = "hidden-transactions"
private const val TAG = "MyApp.FirestoreServ"

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

    fun getSources(userId: String, callback: Callback<Map<String, Account>>){
        firebaseFirestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(ACCOUNTS_COLLECTION)
            .get()
            .addOnSuccessListener {
                val accounts = mutableMapOf<String, Account>()
                it.forEach { document ->
                    accounts[document.id] = document.toObject(Account::class.java)
                }
                callback.onSuccess(accounts)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    fun getUser(userId: String, callback: Callback<User>){
        firebaseFirestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                callback.onSuccess(user)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    fun updateBalance(amount: Double, userId: String, accountId: String, callback: Callback<Void>) {
        Log.d(TAG, "updateBalance: $amount, $userId, $accountId")
        firebaseFirestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(ACCOUNTS_COLLECTION)
            .document(accountId)
            .get()
            .addOnSuccessListener { document ->
                val account = document.toObject(Account::class.java)
                account?.let {
                    val newBalance = (it.getBalance().toDouble() + amount).toString()
                    firebaseFirestore
                        .collection(USERS_COLLECTION)
                        .document(userId)
                        .collection(ACCOUNTS_COLLECTION)
                        .document(accountId)
                        .update("balance", newBalance)
                        .addOnSuccessListener {
                            callback.onSuccess(null)
                        }
                        .addOnFailureListener {
                            callback.onFailure(it)
                        }
                }
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    fun addTransaction(transaction: Transaction, callback: Callback<Void>) {
        firebaseFirestore
            .collection(TRANSACTIONS_COLLECTION)
            .add(transaction.getData())
            .addOnSuccessListener {
                callback.onSuccess(null)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    fun updateField(field: String, value: Any, userId: String, callback: Callback<Void>) {
        firebaseFirestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .update(field, value)
            .addOnSuccessListener {
                callback.onSuccess(null)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    fun addAccount(userId: String, account: Account, callback: Callback<Void>) {
        firebaseFirestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(ACCOUNTS_COLLECTION)
            .document(account.getId())
            .set(account.getData())
            .addOnSuccessListener {
                addSourceTransaction(
                    userId = userId,
                    account = account
                )
                callback.onSuccess(null)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    private fun addSourceTransaction(userId: String, account: Account) {
        val transaction = Transaction(
            userId = userId,
            concept = "Add account: ${account.getName()}",
            category = "Add account",
            type = Constants.TRANSACTION_TYPE_INCOME,
            amount = account.getBalance().toDouble(),
            destination = account.getName(),
        )
        Log.d(TAG, "addSourceTransaction: $transaction")
        firebaseFirestore
            .collection(HIDDEN_TRANSACTIONS_COLLECTION)
            .add(transaction.getData())
    }

}