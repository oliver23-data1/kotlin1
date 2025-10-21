package com.example.app2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.models.Customer

class CustomersAdapter(
    private var customers: MutableList<Customer>,
    private val onItemClick: (Customer) -> Unit,
    private val onEditClick: (Customer) -> Unit,
    private val onDeleteClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder>() {

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvCustomerName)
        val phoneTextView: TextView = itemView.findViewById(R.id.tvCustomerPhone)
        val emailTextView: TextView = itemView.findViewById(R.id.tvCustomerEmail)
        val addressTextView: TextView = itemView.findViewById(R.id.tvCustomerAddress)
        val editButton: View = itemView.findViewById(R.id.btnEdit)
        val deleteButton: View = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
        
        holder.nameTextView.text = customer.name
        holder.phoneTextView.text = customer.phone
        holder.emailTextView.text = customer.email ?: "Sin email"
        holder.addressTextView.text = customer.address

        holder.itemView.setOnClickListener { onItemClick(customer) }
        holder.editButton.setOnClickListener { onEditClick(customer) }
        holder.deleteButton.setOnClickListener { onDeleteClick(customer) }
    }

    override fun getItemCount(): Int = customers.size

    fun updateCustomers(newCustomers: List<Customer>) {
        customers.clear()
        customers.addAll(newCustomers)
        notifyDataSetChanged()
    }

    fun removeCustomer(customer: Customer) {
        val position = customers.indexOf(customer)
        if (position != -1) {
            customers.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}