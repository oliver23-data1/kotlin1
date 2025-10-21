package com.example.app2.ui.reports

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.adapters.SalesAdapter
import com.example.app2.data.database.SaleDao
import com.example.app2.data.models.Sale
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {
    
    private lateinit var btnSelectStartDate: MaterialButton
    private lateinit var btnSelectEndDate: MaterialButton
    private lateinit var tvDateRange: TextView
    private lateinit var tvTotalSales: TextView
    private lateinit var tvSalesCount: TextView
    private lateinit var recyclerViewReportSales: RecyclerView
    private lateinit var btnGenerateReport: MaterialButton
    
    private lateinit var saleDao: SaleDao
    private lateinit var salesAdapter: SalesAdapter
    
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var sales = mutableListOf<Sale>()
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        saleDao = SaleDao(this)
        setupToolbar()
        setupViews()
        setupRecyclerView()
        setupDefaultDates()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViews() {
        btnSelectStartDate = findViewById(R.id.btnSelectStartDate)
        btnSelectEndDate = findViewById(R.id.btnSelectEndDate)
        tvDateRange = findViewById(R.id.tvDateRange)
        tvTotalSales = findViewById(R.id.tvTotalSales)
        tvSalesCount = findViewById(R.id.tvSalesCount)
        recyclerViewReportSales = findViewById(R.id.recyclerViewReportSales)
        btnGenerateReport = findViewById(R.id.btnGenerateReport)

        btnSelectStartDate.setOnClickListener { showDatePicker(true) }
        btnSelectEndDate.setOnClickListener { showDatePicker(false) }
        btnGenerateReport.setOnClickListener { generateReport() }
    }

    private fun setupRecyclerView() {
        salesAdapter = SalesAdapter(
            sales = sales,
            onItemClick = { /* No action needed for reports */ },
            onDeleteClick = { /* No delete action in reports */ }
        )
        recyclerViewReportSales.layoutManager = LinearLayoutManager(this)
        recyclerViewReportSales.adapter = salesAdapter
        
        // Hide delete buttons in reports view
        salesAdapter.updateSales(sales)
    }

    private fun setupDefaultDates() {
        val calendar = Calendar.getInstance()
        
        // End date: today
        endDate = calendar.time
        
        // Start date: 30 days ago
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        startDate = calendar.time
        
        updateDateRangeText()
        generateReport()
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val currentDate = if (isStartDate) startDate else endDate
        currentDate?.let { calendar.time = it }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                
                if (isStartDate) {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    selectedCalendar.set(Calendar.MINUTE, 0)
                    selectedCalendar.set(Calendar.SECOND, 0)
                    selectedCalendar.set(Calendar.MILLISECOND, 0)
                    startDate = selectedCalendar.time
                } else {
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, 23)
                    selectedCalendar.set(Calendar.MINUTE, 59)
                    selectedCalendar.set(Calendar.SECOND, 59)
                    selectedCalendar.set(Calendar.MILLISECOND, 999)
                    endDate = selectedCalendar.time
                }
                
                updateDateRangeText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateRangeText() {
        if (startDate != null && endDate != null) {
            tvDateRange.text = "Período: ${dateFormat.format(startDate!!)} - ${dateFormat.format(endDate!!)}"
        }
    }

    private fun generateReport() {
        if (startDate == null || endDate == null) {
            return
        }

        try {
            // Get sales in date range
            val reportSales = saleDao.getSalesByDateRange(startDate!!, endDate!!)
            sales.clear()
            sales.addAll(reportSales)
            salesAdapter.updateSales(sales)

            // Calculate totals
            val totalAmount = saleDao.getTotalSalesByDateRange(startDate!!, endDate!!)
            val salesCount = sales.size

            // Update UI
            tvTotalSales.text = "Total Vendido: $${String.format("%.2f", totalAmount)}"
            tvSalesCount.text = "Número de Ventas: $salesCount"

        } catch (e: Exception) {
            tvTotalSales.text = "Error al generar reporte: ${e.message}"
            tvSalesCount.text = "Número de Ventas: 0"
        }
    }
}