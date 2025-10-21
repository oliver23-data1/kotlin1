package com.example.app2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.models.Product

class ProductsAdapter(
    private var products: MutableList<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvProductName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tvProductDescription)
        val priceTextView: TextView = itemView.findViewById(R.id.tvProductPrice)
        val stockTextView: TextView = itemView.findViewById(R.id.tvProductStock)
        val categoryTextView: TextView = itemView.findViewById(R.id.tvProductCategory)
        val editButton: View = itemView.findViewById(R.id.btnEdit)
        val deleteButton: View = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        holder.nameTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = "$${String.format("%.2f", product.price)}"
        holder.stockTextView.text = "Stock: ${product.stock}"
        holder.categoryTextView.text = product.category

        holder.itemView.setOnClickListener { onItemClick(product) }
        holder.editButton.setOnClickListener { onEditClick(product) }
        holder.deleteButton.setOnClickListener { onDeleteClick(product) }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun removeProduct(product: Product) {
        val position = products.indexOf(product)
        if (position != -1) {
            products.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}