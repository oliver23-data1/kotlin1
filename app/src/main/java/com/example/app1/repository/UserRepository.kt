package com.example.app1.repository

import android.content.Context
import com.example.app1.database.DatabaseHelper
import com.example.app1.model.User

class UserRepository(context: Context) {
    private val databaseHelper = DatabaseHelper(context)
    
    // Agregar usuario
    fun addUser(user: User): Long {
        return databaseHelper.insertUser(user)
    }
    
    // Obtener todos los usuarios
    fun getAllUsers(): List<User> {
        return databaseHelper.getAllUsers()
    }
    
    // Obtener usuario por ID
    fun getUserById(id: Long): User? {
        return databaseHelper.getUserById(id)
    }
    
    // Actualizar usuario
    fun updateUser(user: User): Boolean {
        return databaseHelper.updateUser(user) > 0
    }
    
    // Eliminar usuario
    fun deleteUser(id: Long): Boolean {
        return databaseHelper.deleteUser(id) > 0
    }
    
    // Eliminar todos los usuarios
    fun deleteAllUsers(): Boolean {
        return databaseHelper.deleteAllUsers() > 0
    }
    
    // Obtener cantidad de usuarios
    fun getUserCount(): Int {
        return databaseHelper.getUserCount()
    }
    
    // Validar si el email ya existe
    fun isEmailExists(email: String, excludeId: Long? = null): Boolean {
        val users = getAllUsers()
        return users.any { user -> 
            user.email.equals(email, ignoreCase = true) && 
            (excludeId == null || user.id != excludeId)
        }
    }
}