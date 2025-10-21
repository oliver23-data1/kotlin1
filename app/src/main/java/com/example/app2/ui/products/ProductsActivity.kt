package com.example.app2.ui.products

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.adapters.ProductsAdapter
import com.example.app2.data.database.ProductDao
import com.example.app2.data.models.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductsActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductsAdapter
    private lateinit var productDao: ProductDao
    private var products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        productDao = ProductDao(this)
        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewProducts)
        adapter = ProductsAdapter(
            products = products,
            onItemClick = { product -> showProductDetails(product) },
            onEditClick = { product -> editProduct(product) },
            onDeleteClick = { product -> confirmDeleteProduct(product) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fabAddProduct).setOnClickListener {
            addProduct()
        }
    }

    private fun loadProducts() {
        try {
            val loadedProducts = productDao.getAllProducts()
            products.clear()
            products.addAll(loadedProducts)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar productos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addProduct() {
        val intent = Intent(this, AddEditProductActivity::class.java)
        startActivity(intent)
    }

    private fun editProduct(product: Product) {
        val intent = Intent(this, AddEditProductActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    private fun showProductDetails(product: Product) {
        val message = """
            Nombre: ${product.name}
            Categoría: ${product.category}
            Descripción: ${product.description}
            Precio: $${String.format("%.2f", product.price)}
            Stock: ${product.stock}
            ${if (product.barcode != null) "Código: ${product.barcode}" else ""}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detalles del Producto")
            .setMessage(message)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun confirmDeleteProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que quieres eliminar '${product.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        try {
            val result = productDao.deleteProduct(product.id)
            if (result > 0) {
                adapter.removeProduct(product)
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al eliminar producto", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}