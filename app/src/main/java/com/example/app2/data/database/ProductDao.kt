package com.example.app2.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.app2.data.models.Product

class ProductDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertProduct(product: Product): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.name)
            put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.description)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.price)
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, product.stock)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.category)
            put(DatabaseHelper.COLUMN_PRODUCT_BARCODE, product.barcode)
        }
        return db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values)
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, null, null, null, null, null, null)
        
        cursor.use {
            while (it.moveToNext()) {
                products.add(cursorToProduct(it))
            }
        }
        return products
    }

    fun getProductById(id: Long): Product? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTS,
            null,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        
        cursor.use {
            return if (it.moveToFirst()) cursorToProduct(it) else null
        }
    }

    fun updateProduct(product: Product): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.name)
            put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.description)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.price)
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, product.stock)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.category)
            put(DatabaseHelper.COLUMN_PRODUCT_BARCODE, product.barcode)
        }
        return db.update(
            DatabaseHelper.TABLE_PRODUCTS,
            values,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(product.id.toString())
        )
    }

    fun deleteProduct(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_PRODUCTS,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun searchProducts(query: String): List<Product> {
        val products = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTS,
            null,
            "${DatabaseHelper.COLUMN_PRODUCT_NAME} LIKE ? OR ${DatabaseHelper.COLUMN_PRODUCT_CATEGORY} LIKE ?",
            arrayOf("%$query%", "%$query%"),
            null, null, null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                products.add(cursorToProduct(it))
            }
        }
        return products
    }

    fun updateStock(productId: Long, newStock: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, newStock)
        }
        return db.update(
            DatabaseHelper.TABLE_PRODUCTS,
            values,
            "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )
    }

    private fun cursorToProduct(cursor: Cursor): Product {
        return Product(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)) ?: "",
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
            stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
            category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
            barcode = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_BARCODE))
        )
    }
}