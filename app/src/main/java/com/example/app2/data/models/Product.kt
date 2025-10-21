package com.example.app2.data.models

data class Product(
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val barcode: String? = null
)