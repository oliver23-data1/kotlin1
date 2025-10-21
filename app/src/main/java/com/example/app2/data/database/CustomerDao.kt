package com.example.app2.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.app2.data.models.Customer

class CustomerDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertCustomer(customer: Customer): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CUSTOMER_NAME, customer.name)
            put(DatabaseHelper.COLUMN_CUSTOMER_EMAIL, customer.email)
            put(DatabaseHelper.COLUMN_CUSTOMER_PHONE, customer.phone)
            put(DatabaseHelper.COLUMN_CUSTOMER_ADDRESS, customer.address)
            put(DatabaseHelper.COLUMN_CUSTOMER_DOCUMENT, customer.document)
        }
        return db.insert(DatabaseHelper.TABLE_CUSTOMERS, null, values)
    }

    fun getAllCustomers(): List<Customer> {
        val customers = mutableListOf<Customer>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_CUSTOMERS, null, null, null, null, null, null)
        
        cursor.use {
            while (it.moveToNext()) {
                customers.add(cursorToCustomer(it))
            }
        }
        return customers
    }

    fun getCustomerById(id: Long): Customer? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CUSTOMERS,
            null,
            "${DatabaseHelper.COLUMN_CUSTOMER_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        
        cursor.use {
            return if (it.moveToFirst()) cursorToCustomer(it) else null
        }
    }

    fun updateCustomer(customer: Customer): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CUSTOMER_NAME, customer.name)
            put(DatabaseHelper.COLUMN_CUSTOMER_EMAIL, customer.email)
            put(DatabaseHelper.COLUMN_CUSTOMER_PHONE, customer.phone)
            put(DatabaseHelper.COLUMN_CUSTOMER_ADDRESS, customer.address)
            put(DatabaseHelper.COLUMN_CUSTOMER_DOCUMENT, customer.document)
        }
        return db.update(
            DatabaseHelper.TABLE_CUSTOMERS,
            values,
            "${DatabaseHelper.COLUMN_CUSTOMER_ID} = ?",
            arrayOf(customer.id.toString())
        )
    }

    fun deleteCustomer(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_CUSTOMERS,
            "${DatabaseHelper.COLUMN_CUSTOMER_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun searchCustomers(query: String): List<Customer> {
        val customers = mutableListOf<Customer>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CUSTOMERS,
            null,
            "${DatabaseHelper.COLUMN_CUSTOMER_NAME} LIKE ? OR ${DatabaseHelper.COLUMN_CUSTOMER_PHONE} LIKE ? OR ${DatabaseHelper.COLUMN_CUSTOMER_DOCUMENT} LIKE ?",
            arrayOf("%$query%", "%$query%", "%$query%"),
            null, null, null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                customers.add(cursorToCustomer(it))
            }
        }
        return customers
    }

    private fun cursorToCustomer(cursor: Cursor): Customer {
        return Customer(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_NAME)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_EMAIL)),
            phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_PHONE)),
            address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_ADDRESS)),
            document = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUSTOMER_DOCUMENT))
        )
    }
}