package com.example.app2.ui.sales

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.adapters.ProductSelectionAdapter
import com.example.app2.adapters.SaleItemDisplay
import com.example.app2.adapters.SaleItemsAdapter
import com.example.app2.data.database.CustomerDao
import com.example.app2.data.database.ProductDao
import com.example.app2.data.database.SaleDao
import com.example.app2.data.models.Customer
import com.example.app2.data.models.Product
import com.example.app2.data.models.Sale
import com.example.app2.data.models.SaleItem
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CreateSaleActivity : AppCompatActivity() {
    
    private lateinit var spinnerCustomer: Spinner
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var recyclerViewSaleItems: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnSaveSale: MaterialButton
    
    private lateinit var customerDao: CustomerDao
    private lateinit var productDao: ProductDao
    private lateinit var saleDao: SaleDao
    
    private lateinit var productAdapter: ProductSelectionAdapter
    private lateinit var saleItemsAdapter: SaleItemsAdapter
    
    private var customers = listOf<Customer>()
    private var products = listOf<Product>()
    private var saleItems = mutableListOf<SaleItemDisplay>()
    private var saleItemsData = mutableListOf<SaleItem>()
    
    private var selectedCustomerId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_sale)

        initializeDAOs()
        setupToolbar()
        setupViews()
        setupRecyclerViews()
        loadData()
    }

    private fun initializeDAOs() {
        customerDao = CustomerDao(this)
        productDao = ProductDao(this)
        saleDao = SaleDao(this)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViews() {
        spinnerCustomer = findViewById(R.id.spinnerCustomer)
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts)
        recyclerViewSaleItems = findViewById(R.id.recyclerViewSaleItems)
        tvTotal = findViewById(R.id.tvTotal)
        btnSaveSale = findViewById(R.id.btnSaveSale)

        btnSaveSale.setOnClickListener { saveSale() }
    }

    private fun setupRecyclerViews() {
        // Products RecyclerView
        productAdapter = ProductSelectionAdapter(
            products = products.toMutableList(),
            onProductSelect = { product -> showAddProductDialog(product) }
        )
        recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        recyclerViewProducts.adapter = productAdapter

        // Sale Items RecyclerView
        saleItemsAdapter = SaleItemsAdapter(
            items = saleItems,
            onRemoveItem = { position -> removeSaleItem(position) }
        )
        recyclerViewSaleItems.layoutManager = LinearLayoutManager(this)
        recyclerViewSaleItems.adapter = saleItemsAdapter
    }

    private fun loadData() {
        loadCustomers()
        loadProducts()
    }

    private fun loadCustomers() {
        try {
            customers = customerDao.getAllCustomers()
            setupCustomerSpinner()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar clientes: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProducts() {
        try {
            products = productDao.getAllProducts()
            productAdapter.updateProducts(products)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar productos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCustomerSpinner() {
        val customerNames = customers.map { "${it.name} - ${it.phone}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, customerNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCustomer.adapter = adapter
        
        spinnerCustomer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position >= 0 && position < customers.size) {
                    selectedCustomerId = customers[position].id
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCustomerId = -1
            }
        }
    }

    private fun showAddProductDialog(product: Product) {
        if (product.stock <= 0) {
            Toast.makeText(this, "Producto sin stock", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product_to_sale, null)
        val etQuantity = dialogView.findViewById<TextInputEditText>(R.id.etQuantity)
        val tvProductName = dialogView.findViewById<TextView>(R.id.tvProductName)
        val tvProductPrice = dialogView.findViewById<TextView>(R.id.tvProductPrice)
        val tvMaxStock = dialogView.findViewById<TextView>(R.id.tvMaxStock)

        tvProductName.text = product.name
        tvProductPrice.text = "Precio: $${String.format("%.2f", product.price)}"
        tvMaxStock.text = "Stock disponible: ${product.stock}"

        AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val quantityStr = etQuantity.text.toString().trim()
                if (quantityStr.isNotEmpty()) {
                    try {
                        val quantity = quantityStr.toInt()
                        if (quantity > 0 && quantity <= product.stock) {
                            addProductToSale(product, quantity)
                        } else {
                            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun addProductToSale(product: Product, quantity: Int) {
        val subtotal = product.price * quantity
        
        // Check if product is already in the sale
        val existingIndex = saleItemsData.indexOfFirst { it.productId == product.id }
        if (existingIndex != -1) {
            // Update existing item
            val existingItem = saleItemsData[existingIndex]
            val newQuantity = existingItem.quantity + quantity
            if (newQuantity <= product.stock) {
                saleItemsData[existingIndex] = existingItem.copy(
                    quantity = newQuantity,
                    subtotal = product.price * newQuantity
                )
                saleItems[existingIndex] = SaleItemDisplay(
                    productName = product.name,
                    quantity = newQuantity,
                    unitPrice = product.price,
                    subtotal = product.price * newQuantity
                )
            } else {
                Toast.makeText(this, "No hay suficiente stock", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            // Add new item
            saleItemsData.add(
                SaleItem(
                    saleId = 0,
                    productId = product.id,
                    quantity = quantity,
                    unitPrice = product.price,
                    subtotal = subtotal
                )
            )
            saleItems.add(
                SaleItemDisplay(
                    productName = product.name,
                    quantity = quantity,
                    unitPrice = product.price,
                    subtotal = subtotal
                )
            )
        }
        
        saleItemsAdapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun removeSaleItem(position: Int) {
        if (position in saleItems.indices) {
            saleItems.removeAt(position)
            saleItemsData.removeAt(position)
            saleItemsAdapter.notifyItemRemoved(position)
            updateTotal()
        }
    }

    private fun updateTotal() {
        val total = saleItems.sumOf { it.subtotal }
        tvTotal.text = "Total: $${String.format("%.2f", total)}"
    }

    private fun saveSale() {
        if (selectedCustomerId == -1L) {
            Toast.makeText(this, "Selecciona un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        if (saleItems.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un producto", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val total = saleItems.sumOf { it.subtotal }
            val sale = Sale(
                customerId = selectedCustomerId,
                date = Date(),
                total = total,
                items = saleItemsData
            )

            val result = saleDao.insertSale(sale)
            if (result > 0) {
                // Update stock for sold products
                for (item in saleItemsData) {
                    val product = productDao.getProductById(item.productId)
                    product?.let {
                        productDao.updateStock(it.id, it.stock - item.quantity)
                    }
                }
                
                Toast.makeText(this, "Venta registrada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar venta", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}