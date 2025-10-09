package com.example.app1.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.app1.model.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "user_database"
        private const val DATABASE_VERSION = 1
        
        // Tabla de usuarios
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_EDAD = "edad"
    }
    
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_EDAD INTEGER NOT NULL
            )
        """.trimIndent()
        
        db?.execSQL(createTableQuery)
    }
    
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
    
    // Crear usuario (CREATE)
    fun insertUser(user: User): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NOMBRE, user.nombre)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_EDAD, user.edad)
        }
        
        val result = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return result
    }
    
    // Leer todos los usuarios (READ)
    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        
        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    edad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EDAD))
                )
                users.add(user)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return users
    }
    
    // Leer usuario por ID (READ)
    fun getUserById(id: Long): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_ID = ?", 
            arrayOf(id.toString())
        )
        
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                edad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EDAD))
            )
        }
        
        cursor.close()
        db.close()
        return user
    }
    
    // Actualizar usuario (UPDATE)
    fun updateUser(user: User): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NOMBRE, user.nombre)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_EDAD, user.edad)
        }
        
        val result = db.update(
            TABLE_USERS, 
            contentValues, 
            "$COLUMN_ID = ?", 
            arrayOf(user.id.toString())
        )
        db.close()
        return result
    }
    
    // Eliminar usuario (DELETE)
    fun deleteUser(id: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
    
    // Eliminar todos los usuarios
    fun deleteAllUsers(): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USERS, null, null)
        db.close()
        return result
    }
    
    // Contar usuarios
    fun getUserCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_USERS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }
}