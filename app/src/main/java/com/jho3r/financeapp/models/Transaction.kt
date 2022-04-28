package com.jho3r.financeapp.models

import com.google.firebase.Timestamp
import java.util.Date

class Transaction(
    private val id: String,
    private val date: Timestamp = Timestamp.now(),
    private val description: String,
    private val category: String,
    // private val companion: Person, TODO: FOR FUTURE USE KNOW HOW MUCH YOU SPENT WITH THIS PERSON
    private val type: String,
    private val amount: Double,
    private val currency: String = "COP",
    private val source: String? = null,
    private val destiny: String? = null,
    private val pending: Boolean = false
) {

}