package com.jho3r.financeapp.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.models.Transaction
import com.jho3r.financeapp.models.User
import com.jho3r.financeapp.utils.Constants
import kotlin.math.absoluteValue

private const val USERS_COLLECTION = "users"
private const val ACCOUNTS_COLLECTION = "accounts"
private const val TRANSACTIONS_COLLECTION = "transactions"
private const val PENDING_TRANSACTIONS_COLLECTION = "pending-transactions"
private const val HIDDEN_TRANSACTIONS_COLLECTION = "hidden-transactions"
private const val ANALYTICS_COLLECTION = "analytics"
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

    // Se llama antes de hacer una transaccion para actualizar el balance de una cuenta
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
                if (transaction.getPending()) {
                    firebaseFirestore
                        .collection(PENDING_TRANSACTIONS_COLLECTION)
                        .add(transaction.getData())
                        .addOnSuccessListener {
                            callback.onSuccess(null)
                        }
                } else {
                    callback.onSuccess(null)
                }
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
                    account = account,
                    difference = account.getBalance().toDouble()
                )
                callback.onSuccess(null)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    fun updateAccount(userId: String, account: Account, callback: Callback<Void>) {
        firebaseFirestore
            .collection(USERS_COLLECTION)
            .document(userId)
            .collection(ACCOUNTS_COLLECTION)
            .document(account.getId())
            .get()
            .addOnSuccessListener {
                val accountData = it.toObject(Account::class.java)
                if (accountData != null) {
                    val lastBalance = accountData.getBalance().toDouble()
                    val newBalance = account.getBalance().toDouble()
                    val difference = newBalance - lastBalance
                    firebaseFirestore
                        .collection(USERS_COLLECTION)
                        .document(userId)
                        .collection(ACCOUNTS_COLLECTION)
                        .document(account.getId())
                        .update(
                            "balance", account.getBalance(),
                            "description", account.getDescription(),
                            "cash", account.isCash()
                        )
                        .addOnSuccessListener {
                            addSourceTransaction(
                                userId = userId,
                                account = account,
                                difference = difference
                            )
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

    private fun addSourceTransaction(userId: String, account: Account, difference: Double = 0.0) {
        val type = if (difference > 0) {
            Constants.TRANSACTION_TYPE_INCOME
        } else {
            Constants.TRANSACTION_TYPE_EXPENSE
        }

        val destination = if (difference > 0) {
            account.getId()
        } else {
            null
        }

        val source = if (difference > 0) {
            null
        } else {
            account.getId()
        }

        if (account.getBalance().toDouble() > 0 && difference.toInt() != 0) {
            val transaction = Transaction(
                userId = userId,
                concept = "Modify account: ${account.getName()}",
                category = "Modify account",
                type = type,
                amount = difference.absoluteValue,
                destination = destination,
                source = source,
            )
            Log.d(TAG, "addSourceTransaction: $transaction")
            firebaseFirestore
                .collection(HIDDEN_TRANSACTIONS_COLLECTION)
                .add(transaction.getData())
        }

    }

}