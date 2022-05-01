package com.jho3r.financeapp.models

import com.google.firebase.Timestamp

class User(
    val id: String,
    private var username: String,
    private var accounts: MutableMap<String, Account> = mutableMapOf(
        "cash" to Account(
            id = "cash",
            name = "Cash",
            balance = "0.00",
            currency = "COP",
            cash = true,
            description = "Cash account",
        ),
        "savings" to Account(
            id = "savings",
            name = "Savings",
            balance = "0.00",
            currency = "COP",
            cash = true,
            description = "Savings under the pillow account",
        ),
    ),
    private var categories: MutableList<String> = mutableListOf(
        "Food",
        "Transport",
        "Debts",
        "Loans",
        "Other"
    ),
    private var dateCreated: Timestamp = Timestamp.now(),
) {

    constructor() : this("", "")

    fun getData(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "username" to username,
            "categories" to categories,
            "dateCreated" to dateCreated
        )
    }
    fun getAccounts(): Map<String, Account> {
        return accounts
    }
    fun getCategories(): MutableList<String> {
        return categories
    }
}