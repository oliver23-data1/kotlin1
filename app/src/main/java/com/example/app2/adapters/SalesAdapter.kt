package com.example.app2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.models.Sale
import java.text.SimpleDateFormat
import java.util.Locale

class SalesAdapter(
    private var sales: MutableList<Sale>,
    private val onItemClick: (Sale) -> Unit,
    private val onDeleteClick: (Sale) -> Unit
) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.tvSaleDate)
        val totalTextView: TextView = itemView.findViewById(R.id.tvSaleTotal)
        val customerTextView: TextView = itemView.findViewById(R.id.tvSaleCustomer)
        val itemsTextView: TextView = itemView.findViewById(R.id.tvSaleItems)
        val deleteButton: View = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = sales[position]
        
        holder.dateTextView.text = dateFormat.format(sale.date)
        holder.totalTextView.text = "$${String.format("%.2f", sale.total)}"
        holder.customerTextView.text = "Cliente ID: ${sale.customerId}"
        holder.itemsTextView.text = "${sale.items.size} productos"

        holder.itemView.setOnClickListener { onItemClick(sale) }
        holder.deleteButton.setOnClickListener { onDeleteClick(sale) }
    }

    override fun getItemCount(): Int = sales.size

    fun updateSales(newSales: List<Sale>) {
        sales.clear()
        sales.addAll(newSales)
        notifyDataSetChanged()
    }

    fun removeSale(sale: Sale) {
        val position = sales.indexOf(sale)
        if (position != -1) {
            sales.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}