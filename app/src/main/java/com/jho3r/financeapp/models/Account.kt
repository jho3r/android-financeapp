package com.jho3r.financeapp.models

class Account(
    private val id: String,
    private val name: String,
    private val balance: String,
    val currency: String = "COP",
    private val cash: Boolean,
    private val description : String
) {
    constructor() : this("", "", "", "", false, "")
    fun getData() : Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "balance" to balance,
            "currency" to currency,
            "cash" to cash,
            "description" to description
        )
    }

    fun getId() : String {
        return id
    }

    fun getName() : String {
        return name
    }

    fun getBalance() : String {
        return balance
    }

    fun getDescription() : String {
        return description
    }

    fun isCash() : Boolean {
        return cash
    }

}