package com.example.app2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.models.Product

class ProductSelectionAdapter(
    private var products: MutableList<Product>,
    private val onProductSelect: (Product) -> Unit
) : RecyclerView.Adapter<ProductSelectionAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvProductName)
        val priceTextView: TextView = itemView.findViewById(R.id.tvProductPrice)
        val stockTextView: TextView = itemView.findViewById(R.id.tvProductStock)
        val categoryTextView: TextView = itemView.findViewById(R.id.tvProductCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_selection, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        holder.nameTextView.text = product.name
        holder.priceTextView.text = "$${String.format("%.2f", product.price)}"
        holder.stockTextView.text = "Stock: ${product.stock}"
        holder.categoryTextView.text = product.category

        holder.itemView.setOnClickListener { onProductSelect(product) }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}