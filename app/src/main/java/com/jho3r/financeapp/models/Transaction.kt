package com.jho3r.financeapp.models

import com.google.firebase.Timestamp
import java.util.Date

class Transaction(
    private val userId: String,
    private val date: Timestamp = Timestamp.now(),
    private val concept: String,
    private val category: String,
    // private val companion: Person, TODO: FOR FUTURE USE KNOW HOW MUCH YOU SPENT WITH THIS PERSON
    private val type: String,
    private val amount: Double,
    private val currency: String = "COP",
    private val source: String? = null,
    private val destination: String? = null,
    private val isPending: Boolean = false
) {
    fun getUserId(): String {
        return userId
    }

    fun getType(): String {
        return type
    }

    fun getPending(): Boolean {
        return isPending
    }

    fun getData(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "date" to date,
            "concept" to concept,
            "category" to category,
            "type" to type,
            "amount" to amount,
            "currency" to currency,
            "source" to source,
            "destination" to destination,
            "isPending" to isPending
        )
    }

}