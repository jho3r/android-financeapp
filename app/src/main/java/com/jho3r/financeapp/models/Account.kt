package com.jho3r.financeapp.models

class Account(
    private var id: String,
    private var name: String,
    private var balance: String,
    var currency: String = "COP",
    private var cash: Boolean = true,
    private var description : String
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

    fun setBalance(balance: String) {
        this.balance = balance
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setCash(cash: Boolean) {
        this.cash = cash
    }

}