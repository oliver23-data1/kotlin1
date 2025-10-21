package com.example.app2.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ferreteria.db"
        const val DATABASE_VERSION = 1

        // Products table
        const val TABLE_PRODUCTS = "products"
        const val COLUMN_PRODUCT_ID = "id"
        const val COLUMN_PRODUCT_NAME = "name"
        const val COLUMN_PRODUCT_DESCRIPTION = "description"
        const val COLUMN_PRODUCT_PRICE = "price"
        const val COLUMN_PRODUCT_STOCK = "stock"
        const val COLUMN_PRODUCT_CATEGORY = "category"
        const val COLUMN_PRODUCT_BARCODE = "barcode"

        // Customers table
        const val TABLE_CUSTOMERS = "customers"
        const val COLUMN_CUSTOMER_ID = "id"
        const val COLUMN_CUSTOMER_NAME = "name"
        const val COLUMN_CUSTOMER_EMAIL = "email"
        const val COLUMN_CUSTOMER_PHONE = "phone"
        const val COLUMN_CUSTOMER_ADDRESS = "address"
        const val COLUMN_CUSTOMER_DOCUMENT = "document"

        // Sales table
        const val TABLE_SALES = "sales"
        const val COLUMN_SALE_ID = "id"
        const val COLUMN_SALE_CUSTOMER_ID = "customer_id"
        const val COLUMN_SALE_DATE = "date"
        const val COLUMN_SALE_TOTAL = "total"

        // Sale Items table
        const val TABLE_SALE_ITEMS = "sale_items"
        const val COLUMN_SALE_ITEM_ID = "id"
        const val COLUMN_SALE_ITEM_SALE_ID = "sale_id"
        const val COLUMN_SALE_ITEM_PRODUCT_ID = "product_id"
        const val COLUMN_SALE_ITEM_QUANTITY = "quantity"
        const val COLUMN_SALE_ITEM_UNIT_PRICE = "unit_price"
        const val COLUMN_SALE_ITEM_SUBTOTAL = "subtotal"

        // Create tables SQL
        private const val CREATE_PRODUCTS_TABLE = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_DESCRIPTION TEXT,
                $COLUMN_PRODUCT_PRICE REAL NOT NULL,
                $COLUMN_PRODUCT_STOCK INTEGER NOT NULL DEFAULT 0,
                $COLUMN_PRODUCT_CATEGORY TEXT NOT NULL,
                $COLUMN_PRODUCT_BARCODE TEXT UNIQUE
            )
        """

        private const val CREATE_CUSTOMERS_TABLE = """
            CREATE TABLE $TABLE_CUSTOMERS (
                $COLUMN_CUSTOMER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CUSTOMER_NAME TEXT NOT NULL,
                $COLUMN_CUSTOMER_EMAIL TEXT,
                $COLUMN_CUSTOMER_PHONE TEXT NOT NULL,
                $COLUMN_CUSTOMER_ADDRESS TEXT NOT NULL,
                $COLUMN_CUSTOMER_DOCUMENT TEXT UNIQUE
            )
        """

        private const val CREATE_SALES_TABLE = """
            CREATE TABLE $TABLE_SALES (
                $COLUMN_SALE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SALE_CUSTOMER_ID INTEGER NOT NULL,
                $COLUMN_SALE_DATE INTEGER NOT NULL,
                $COLUMN_SALE_TOTAL REAL NOT NULL,
                FOREIGN KEY($COLUMN_SALE_CUSTOMER_ID) REFERENCES $TABLE_CUSTOMERS($COLUMN_CUSTOMER_ID)
            )
        """

        private const val CREATE_SALE_ITEMS_TABLE = """
            CREATE TABLE $TABLE_SALE_ITEMS (
                $COLUMN_SALE_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SALE_ITEM_SALE_ID INTEGER NOT NULL,
                $COLUMN_SALE_ITEM_PRODUCT_ID INTEGER NOT NULL,
                $COLUMN_SALE_ITEM_QUANTITY INTEGER NOT NULL,
                $COLUMN_SALE_ITEM_UNIT_PRICE REAL NOT NULL,
                $COLUMN_SALE_ITEM_SUBTOTAL REAL NOT NULL,
                FOREIGN KEY($COLUMN_SALE_ITEM_SALE_ID) REFERENCES $TABLE_SALES($COLUMN_SALE_ID),
                FOREIGN KEY($COLUMN_SALE_ITEM_PRODUCT_ID) REFERENCES $TABLE_PRODUCTS($COLUMN_PRODUCT_ID)
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_PRODUCTS_TABLE)
        db?.execSQL(CREATE_CUSTOMERS_TABLE)
        db?.execSQL(CREATE_SALES_TABLE)
        db?.execSQL(CREATE_SALE_ITEMS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SALE_ITEMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SALES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys=ON")
    }
}