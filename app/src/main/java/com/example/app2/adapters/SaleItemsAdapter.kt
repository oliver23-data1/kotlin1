package com.example.app2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R

data class SaleItemDisplay(
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

class SaleItemsAdapter(
    private var items: MutableList<SaleItemDisplay>,
    private val onRemoveItem: (Int) -> Unit
) : RecyclerView.Adapter<SaleItemsAdapter.SaleItemViewHolder>() {

    class SaleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.tvItemProductName)
        val quantityTextView: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val unitPriceTextView: TextView = itemView.findViewById(R.id.tvItemUnitPrice)
        val subtotalTextView: TextView = itemView.findViewById(R.id.tvItemSubtotal)
        val removeButton: View = itemView.findViewById(R.id.btnRemoveItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale_item, parent, false)
        return SaleItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleItemViewHolder, position: Int) {
        val item = items[position]
        
        holder.productNameTextView.text = item.productName
        holder.quantityTextView.text = "${item.quantity}"
        holder.unitPriceTextView.text = "$${String.format("%.2f", item.unitPrice)}"
        holder.subtotalTextView.text = "$${String.format("%.2f", item.subtotal)}"

        holder.removeButton.setOnClickListener { onRemoveItem(position) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<SaleItemDisplay>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}