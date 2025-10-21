package com.example.app2.data.models

data class Customer(
    val id: Long = 0,
    val name: String,
    val email: String? = null,
    val phone: String,
    val address: String,
    val document: String? = null
)