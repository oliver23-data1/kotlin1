package com.example.app2.data.models

import java.util.Date

data class Sale(
    val id: Long = 0,
    val customerId: Long,
    val date: Date,
    val total: Double,
    val items: List<SaleItem> = emptyList()
)