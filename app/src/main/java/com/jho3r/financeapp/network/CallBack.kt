package com.jho3r.financeapp.network

interface Callback<T> {
    fun onSuccess(response: T?)
    fun onFailure(exception: Exception)
}