package com.example.app1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app1.R
import com.example.app1.model.User

class UserAdapter(
    private var users: MutableList<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        val tvUserAge: TextView = itemView.findViewById(R.id.tvUserAge)
        val btnEditUser: ImageButton = itemView.findViewById(R.id.btnEditUser)
        val btnDeleteUser: ImageButton = itemView.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        
        holder.tvUserName.text = user.nombre
        holder.tvUserEmail.text = user.email
        holder.tvUserAge.text = "Edad: ${user.edad} años"
        
        // Configurar click listeners
        holder.btnEditUser.setOnClickListener {
            onEditClick(user)
        }
        
        holder.btnDeleteUser.setOnClickListener {
            onDeleteClick(user)
        }
        
        // Click en toda la tarjeta para ver detalles
        holder.itemView.setOnClickListener {
            onEditClick(user)
        }
    }

    override fun getItemCount(): Int = users.size

    // Métodos para actualizar la lista
    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }
    
    fun addUser(user: User) {
        users.add(user)
        notifyItemInserted(users.size - 1)
    }
    
    fun updateUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
            notifyItemChanged(index)
        }
    }
    
    fun removeUser(user: User) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    
    fun getUserAt(position: Int): User? {
        return if (position in 0 until users.size) users[position] else null
    }
}