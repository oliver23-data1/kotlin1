package com.example.app1

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app1.adapter.UserAdapter
import com.example.app1.model.User
import com.example.app1.repository.UserRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    
    private lateinit var userRepository: UserRepository
    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var fabAddUser: FloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initializeComponents()
        setupRecyclerView()
        setupClickListeners()
        loadUsers()
    }
    
    private fun initializeComponents() {
        userRepository = UserRepository(this)
        recyclerView = findViewById(R.id.recyclerViewUsers)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        fabAddUser = findViewById(R.id.fabAddUser)
    }
    
    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            users = mutableListOf(),
            onEditClick = { user -> showUserDialog(user) },
            onDeleteClick = { user -> showDeleteConfirmation(user) }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userAdapter
        }
    }
    
    private fun setupClickListeners() {
        fabAddUser.setOnClickListener {
            showUserDialog(null) // null para nuevo usuario
        }
    }
    
    private fun loadUsers() {
        val users = userRepository.getAllUsers()
        userAdapter.updateUsers(users)
        updateEmptyMessage(users.isEmpty())
    }
    
    private fun updateEmptyMessage(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = android.view.View.GONE
            tvEmptyMessage.visibility = android.view.View.VISIBLE
        } else {
            recyclerView.visibility = android.view.View.VISIBLE
            tvEmptyMessage.visibility = android.view.View.GONE
        }
    }
    
    private fun showUserDialog(user: User?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_form, null)
        
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etUserName = dialogView.findViewById<TextInputEditText>(R.id.etUserName)
        val etUserEmail = dialogView.findViewById<TextInputEditText>(R.id.etUserEmail)
        val etUserAge = dialogView.findViewById<TextInputEditText>(R.id.etUserAge)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        
        // Configurar título y datos si es edición
        if (user != null) {
            tvDialogTitle.text = "Editar Usuario"
            etUserName.setText(user.nombre)
            etUserEmail.setText(user.email)
            etUserAge.setText(user.edad.toString())
            btnSave.text = "Actualizar"
        } else {
            tvDialogTitle.text = "Agregar Usuario"
            btnSave.text = "Guardar"
        }
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        btnSave.setOnClickListener {
            val nombre = etUserName.text.toString().trim()
            val email = etUserEmail.text.toString().trim()
            val edadText = etUserAge.text.toString().trim()
            
            if (validateUserInput(nombre, email, edadText, user?.id)) {
                val edad = edadText.toInt()
                
                if (user != null) {
                    // Actualizar usuario existente
                    val updatedUser = user.copy(nombre = nombre, email = email, edad = edad)
                    if (userRepository.updateUser(updatedUser)) {
                        userAdapter.updateUser(updatedUser)
                        Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Crear nuevo usuario
                    val newUser = User(nombre = nombre, email = email, edad = edad)
                    val id = userRepository.addUser(newUser)
                    if (id > 0) {
                        val createdUser = newUser.copy(id = id)
                        userAdapter.addUser(createdUser)
                        updateEmptyMessage(false)
                        Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Error al agregar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        
        dialog.show()
    }
    
    private fun validateUserInput(nombre: String, email: String, edadText: String, excludeId: Long?): Boolean {
        when {
            nombre.isEmpty() -> {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                return false
            }
            nombre.length < 2 -> {
                Toast.makeText(this, "El nombre debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
                return false
            }
            email.isEmpty() -> {
                Toast.makeText(this, "El email es obligatorio", Toast.LENGTH_SHORT).show()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "El email no tiene un formato válido", Toast.LENGTH_SHORT).show()
                return false
            }
            userRepository.isEmailExists(email, excludeId) -> {
                Toast.makeText(this, "Este email ya está registrado", Toast.LENGTH_SHORT).show()
                return false
            }
            edadText.isEmpty() -> {
                Toast.makeText(this, "La edad es obligatoria", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {
                try {
                    val edad = edadText.toInt()
                    if (edad < 1 || edad > 120) {
                        Toast.makeText(this, "La edad debe estar entre 1 y 120 años", Toast.LENGTH_SHORT).show()
                        return false
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "La edad debe ser un número válido", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }
    
    private fun showDeleteConfirmation(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar a ${user.nombre}?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun deleteUser(user: User) {
        if (userRepository.deleteUser(user.id)) {
            userAdapter.removeUser(user)
            val remainingUsers = userRepository.getAllUsers()
            updateEmptyMessage(remainingUsers.isEmpty())
            Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Recargar usuarios en caso de que hayan cambiado
        loadUsers()
    }
}