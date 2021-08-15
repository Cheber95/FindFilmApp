package ru.chebertests.findfilmapp.model

interface Callback<T> {
    fun onSuccess(type: T)
}