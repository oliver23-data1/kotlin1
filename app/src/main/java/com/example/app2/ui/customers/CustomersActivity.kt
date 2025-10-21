package com.example.app2.ui.customers

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.adapters.CustomersAdapter
import com.example.app2.data.database.CustomerDao
import com.example.app2.data.models.Customer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomersActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomersAdapter
    private lateinit var customerDao: CustomerDao
    private var customers = mutableListOf<Customer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customers)

        customerDao = CustomerDao(this)
        setupToolbar()
        setupRecyclerView()
        setupFab()
        loadCustomers()
    }

    override fun onResume() {
        super.onResume()
        loadCustomers()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewCustomers)
        adapter = CustomersAdapter(
            customers = customers,
            onItemClick = { customer -> showCustomerDetails(customer) },
            onEditClick = { customer -> editCustomer(customer) },
            onDeleteClick = { customer -> confirmDeleteCustomer(customer) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fabAddCustomer).setOnClickListener {
            addCustomer()
        }
    }

    private fun loadCustomers() {
        try {
            val loadedCustomers = customerDao.getAllCustomers()
            customers.clear()
            customers.addAll(loadedCustomers)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar clientes: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addCustomer() {
        val intent = Intent(this, AddEditCustomerActivity::class.java)
        startActivity(intent)
    }

    private fun editCustomer(customer: Customer) {
        val intent = Intent(this, AddEditCustomerActivity::class.java)
        intent.putExtra("customer_id", customer.id)
        startActivity(intent)
    }

    private fun showCustomerDetails(customer: Customer) {
        val message = """
            Nombre: ${customer.name}
            Teléfono: ${customer.phone}
            Email: ${customer.email ?: "No especificado"}
            Dirección: ${customer.address}
            ${if (customer.document != null) "Documento: ${customer.document}" else ""}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Detalles del Cliente")
            .setMessage(message)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun confirmDeleteCustomer(customer: Customer) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cliente")
            .setMessage("¿Estás seguro de que quieres eliminar '${customer.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteCustomer(customer)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteCustomer(customer: Customer) {
        try {
            val result = customerDao.deleteCustomer(customer.id)
            if (result > 0) {
                adapter.removeCustomer(customer)
                Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al eliminar cliente", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}