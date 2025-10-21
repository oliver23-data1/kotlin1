package com.example.app2.ui.sales

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.adapters.SalesAdapter
import com.example.app2.data.database.CustomerDao
import com.example.app2.data.database.SaleDao
import com.example.app2.data.models.Sale
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Locale

class SalesActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SalesAdapter
    private lateinit var saleDao: SaleDao
    private lateinit var customerDao: CustomerDao
    private var sales = mutableListOf<Sale>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        saleDao = SaleDao(this)
        customerDao = CustomerDao(this)
        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadSales()
    }

    override fun onResume() {
        super.onResume()
        loadSales()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewSales)
        adapter = SalesAdapter(
            sales = sales,
            onItemClick = { sale -> showSaleDetails(sale) },
            onDeleteClick = { sale -> confirmDeleteSale(sale) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fabAddSale).setOnClickListener {
            createNewSale()
        }
    }

    private fun loadSales() {
        try {
            val loadedSales = saleDao.getAllSales()
            sales.clear()
            sales.addAll(loadedSales)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar ventas: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewSale() {
        val intent = Intent(this, CreateSaleActivity::class.java)
        startActivity(intent)
    }

    private fun showSaleDetails(sale: Sale) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val customer = customerDao.getCustomerById(sale.customerId)
        
        val itemsText = sale.items.joinToString("\n") { item ->
            "• Cantidad: ${item.quantity} - Precio: $${String.format("%.2f", item.unitPrice)} - Subtotal: $${String.format("%.2f", item.subtotal)}"
        }

        val message = """
            Fecha: ${dateFormat.format(sale.date)}
            Cliente: ${customer?.name ?: "Desconocido"}
            
            Productos:
            $itemsText
            
            TOTAL: $${String.format("%.2f", sale.total)}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detalles de la Venta #${sale.id}")
            .setMessage(message)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun confirmDeleteSale(sale: Sale) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Venta")
            .setMessage("¿Estás seguro de que quieres eliminar esta venta? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteSale(sale)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteSale(sale: Sale) {
        try {
            val result = saleDao.deleteSale(sale.id)
            if (result > 0) {
                adapter.removeSale(sale)
                Toast.makeText(this, "Venta eliminada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al eliminar venta", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}