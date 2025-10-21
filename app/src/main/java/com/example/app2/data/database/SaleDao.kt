package com.example.app2.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.app2.data.models.Sale
import com.example.app2.data.models.SaleItem
import java.util.Date

class SaleDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertSale(sale: Sale): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        
        try {
            // Insert sale
            val saleValues = ContentValues().apply {
                put(DatabaseHelper.COLUMN_SALE_CUSTOMER_ID, sale.customerId)
                put(DatabaseHelper.COLUMN_SALE_DATE, sale.date.time)
                put(DatabaseHelper.COLUMN_SALE_TOTAL, sale.total)
            }
            val saleId = db.insert(DatabaseHelper.TABLE_SALES, null, saleValues)
            
            // Insert sale items
            for (item in sale.items) {
                val itemValues = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_SALE_ITEM_SALE_ID, saleId)
                    put(DatabaseHelper.COLUMN_SALE_ITEM_PRODUCT_ID, item.productId)
                    put(DatabaseHelper.COLUMN_SALE_ITEM_QUANTITY, item.quantity)
                    put(DatabaseHelper.COLUMN_SALE_ITEM_UNIT_PRICE, item.unitPrice)
                    put(DatabaseHelper.COLUMN_SALE_ITEM_SUBTOTAL, item.subtotal)
                }
                db.insert(DatabaseHelper.TABLE_SALE_ITEMS, null, itemValues)
            }
            
            db.setTransactionSuccessful()
            return saleId
        } finally {
            db.endTransaction()
        }
    }

    fun getAllSales(): List<Sale> {
        val sales = mutableListOf<Sale>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_SALES, null, null, null, null, null, "${DatabaseHelper.COLUMN_SALE_DATE} DESC")
        
        cursor.use {
            while (it.moveToNext()) {
                val sale = cursorToSale(it)
                val items = getSaleItems(sale.id)
                sales.add(sale.copy(items = items))
            }
        }
        return sales
    }

    fun getSaleById(id: Long): Sale? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_SALES,
            null,
            "${DatabaseHelper.COLUMN_SALE_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        
        cursor.use {
            return if (it.moveToFirst()) {
                val sale = cursorToSale(it)
                val items = getSaleItems(sale.id)
                sale.copy(items = items)
            } else null
        }
    }

    private fun getSaleItems(saleId: Long): List<SaleItem> {
        val items = mutableListOf<SaleItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_SALE_ITEMS,
            null,
            "${DatabaseHelper.COLUMN_SALE_ITEM_SALE_ID} = ?",
            arrayOf(saleId.toString()),
            null, null, null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                items.add(cursorToSaleItem(it))
            }
        }
        return items
    }

    fun getSalesByDateRange(startDate: Date, endDate: Date): List<Sale> {
        val sales = mutableListOf<Sale>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_SALES,
            null,
            "${DatabaseHelper.COLUMN_SALE_DATE} BETWEEN ? AND ?",
            arrayOf(startDate.time.toString(), endDate.time.toString()),
            null, null,
            "${DatabaseHelper.COLUMN_SALE_DATE} DESC"
        )
        
        cursor.use {
            while (it.moveToNext()) {
                val sale = cursorToSale(it)
                val items = getSaleItems(sale.id)
                sales.add(sale.copy(items = items))
            }
        }
        return sales
    }

    fun getTotalSalesByDateRange(startDate: Date, endDate: Date): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(${DatabaseHelper.COLUMN_SALE_TOTAL}) FROM ${DatabaseHelper.TABLE_SALES} WHERE ${DatabaseHelper.COLUMN_SALE_DATE} BETWEEN ? AND ?",
            arrayOf(startDate.time.toString(), endDate.time.toString())
        )
        
        cursor.use {
            return if (it.moveToFirst() && !it.isNull(0)) {
                it.getDouble(0)
            } else 0.0
        }
    }

    fun deleteSale(id: Long): Int {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        
        try {
            // Delete sale items first
            db.delete(
                DatabaseHelper.TABLE_SALE_ITEMS,
                "${DatabaseHelper.COLUMN_SALE_ITEM_SALE_ID} = ?",
                arrayOf(id.toString())
            )
            
            // Delete sale
            val result = db.delete(
                DatabaseHelper.TABLE_SALES,
                "${DatabaseHelper.COLUMN_SALE_ID} = ?",
                arrayOf(id.toString())
            )
            
            db.setTransactionSuccessful()
            return result
        } finally {
            db.endTransaction()
        }
    }

    private fun cursorToSale(cursor: Cursor): Sale {
        return Sale(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_ID)),
            customerId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_CUSTOMER_ID)),
            date = Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_DATE))),
            total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_TOTAL))
        )
    }

    private fun cursorToSaleItem(cursor: Cursor): SaleItem {
        return SaleItem(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_ITEM_ID)),
            saleId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_ITEM_SALE_ID)),
            productId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_ITEM_PRODUCT_ID)),
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_ITEM_QUANTITY)),
            unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_ITEM_UNIT_PRICE)),
            subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SALE_ITEM_SUBTOTAL))
        )
    }
}