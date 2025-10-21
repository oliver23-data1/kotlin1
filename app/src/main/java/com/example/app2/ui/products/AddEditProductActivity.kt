package com.example.app2.ui.products

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app2.R
import com.example.app2.data.database.ProductDao
import com.example.app2.data.models.Product
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddEditProductActivity : AppCompatActivity() {
    
    private lateinit var etName: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etPrice: TextInputEditText
    private lateinit var etStock: TextInputEditText
    private lateinit var etBarcode: TextInputEditText
    private lateinit var spinnerCategory: AutoCompleteTextView
    private lateinit var btnSave: MaterialButton
    
    private lateinit var productDao: ProductDao
    private var productId: Long = -1
    private var isEditMode = false

    private val categories = arrayOf(
        "Herramientas", "Ferretería", "Plomería", "Electricidad", 
        "Pintura", "Construcción", "Jardinería", "Otros"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)

        productDao = ProductDao(this)
        
        // Verificar si es modo edición
        productId = intent.getLongExtra("product_id", -1)
        isEditMode = productId != -1L

        setupToolbar()
        setupViews()
        setupCategorySpinner()
        
        if (isEditMode) {
            loadProductData()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        val title = if (isEditMode) "Editar Producto" else "Agregar Producto"
        toolbar.title = title
        
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViews() {
        etName = findViewById(R.id.etProductName)
        etDescription = findViewById(R.id.etProductDescription)
        etPrice = findViewById(R.id.etProductPrice)
        etStock = findViewById(R.id.etProductStock)
        etBarcode = findViewById(R.id.etProductBarcode)
        spinnerCategory = findViewById(R.id.spinnerProductCategory)
        btnSave = findViewById(R.id.btnSaveProduct)

        btnSave.setOnClickListener { saveProduct() }
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        spinnerCategory.setAdapter(adapter)
    }

    private fun loadProductData() {
        try {
            val product = productDao.getProductById(productId)
            product?.let {
                etName.setText(it.name)
                etDescription.setText(it.description)
                etPrice.setText(it.price.toString())
                etStock.setText(it.stock.toString())
                etBarcode.setText(it.barcode ?: "")
                spinnerCategory.setText(it.category, false)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar producto: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveProduct() {
        if (!validateInputs()) return

        try {
            val product = Product(
                id = if (isEditMode) productId else 0,
                name = etName.text.toString().trim(),
                description = etDescription.text.toString().trim(),
                price = etPrice.text.toString().toDouble(),
                stock = etStock.text.toString().toInt(),
                category = spinnerCategory.text.toString(),
                barcode = etBarcode.text.toString().trim().takeIf { it.isNotEmpty() }
            )

            val result = if (isEditMode) {
                productDao.updateProduct(product).toLong()
            } else {
                productDao.insertProduct(product)
            }

            if (result > 0) {
                val message = if (isEditMode) "Producto actualizado" else "Producto agregado"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar producto", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Por favor verifica los valores numéricos", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (etName.text.toString().trim().isEmpty()) {
            etName.error = "El nombre es requerido"
            isValid = false
        }

        if (etPrice.text.toString().trim().isEmpty()) {
            etPrice.error = "El precio es requerido"
            isValid = false
        } else {
            try {
                val price = etPrice.text.toString().toDouble()
                if (price < 0) {
                    etPrice.error = "El precio debe ser mayor a 0"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                etPrice.error = "Precio inválido"
                isValid = false
            }
        }

        if (etStock.text.toString().trim().isEmpty()) {
            etStock.error = "El stock es requerido"
            isValid = false
        } else {
            try {
                val stock = etStock.text.toString().toInt()
                if (stock < 0) {
                    etStock.error = "El stock debe ser mayor o igual a 0"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                etStock.error = "Stock inválido"
                isValid = false
            }
        }

        if (spinnerCategory.text.toString().trim().isEmpty()) {
            spinnerCategory.error = "La categoría es requerida"
            isValid = false
        }

        return isValid
    }
}