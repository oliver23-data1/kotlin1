package com.example.app2.ui.customers

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app2.R
import com.example.app2.data.database.CustomerDao
import com.example.app2.data.models.Customer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddEditCustomerActivity : AppCompatActivity() {
    
    private lateinit var etName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etDocument: TextInputEditText
    private lateinit var btnSave: MaterialButton
    
    private lateinit var customerDao: CustomerDao
    private var customerId: Long = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_customer)

        customerDao = CustomerDao(this)
        
        // Verificar si es modo edición
        customerId = intent.getLongExtra("customer_id", -1)
        isEditMode = customerId != -1L

        setupToolbar()
        setupViews()
        
        if (isEditMode) {
            loadCustomerData()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        val title = if (isEditMode) "Editar Cliente" else "Agregar Cliente"
        toolbar.title = title
        
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViews() {
        etName = findViewById(R.id.etCustomerName)
        etPhone = findViewById(R.id.etCustomerPhone)
        etEmail = findViewById(R.id.etCustomerEmail)
        etAddress = findViewById(R.id.etCustomerAddress)
        etDocument = findViewById(R.id.etCustomerDocument)
        btnSave = findViewById(R.id.btnSaveCustomer)

        btnSave.setOnClickListener { saveCustomer() }
    }

    private fun loadCustomerData() {
        try {
            val customer = customerDao.getCustomerById(customerId)
            customer?.let {
                etName.setText(it.name)
                etPhone.setText(it.phone)
                etEmail.setText(it.email ?: "")
                etAddress.setText(it.address)
                etDocument.setText(it.document ?: "")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar cliente: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveCustomer() {
        if (!validateInputs()) return

        try {
            val customer = Customer(
                id = if (isEditMode) customerId else 0,
                name = etName.text.toString().trim(),
                phone = etPhone.text.toString().trim(),
                email = etEmail.text.toString().trim().takeIf { it.isNotEmpty() },
                address = etAddress.text.toString().trim(),
                document = etDocument.text.toString().trim().takeIf { it.isNotEmpty() }
            )

            val result = if (isEditMode) {
                customerDao.updateCustomer(customer).toLong()
            } else {
                customerDao.insertCustomer(customer)
            }

            if (result > 0) {
                val message = if (isEditMode) "Cliente actualizado" else "Cliente agregado"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al guardar cliente", Toast.LENGTH_SHORT).show()
            }
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

        if (etPhone.text.toString().trim().isEmpty()) {
            etPhone.error = "El teléfono es requerido"
            isValid = false
        }

        val email = etEmail.text.toString().trim()
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email inválido"
            isValid = false
        }

        if (etAddress.text.toString().trim().isEmpty()) {
            etAddress.error = "La dirección es requerida"
            isValid = false
        }

        return isValid
    }
}