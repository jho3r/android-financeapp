package com.jho3r.financeapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.jho3r.financeapp.R

class NewTransactionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)

        supportActionBar?.title = getString(R.string.newtransc_title)

        var etConcept = findViewById<EditText>(R.id.etNewTranscConcept)
        var actvCategories = findViewById<AutoCompleteTextView>(R.id.actvNewTranscCategory)
        var spinnerTypes = findViewById<Spinner>(R.id.spNewTranscType)
        var sppinerSources = findViewById<Spinner>(R.id.spNewTranscSource)
        var spinnerDestinations = findViewById<Spinner>(R.id.spNewTranscDestination)

        var categories = listOf<String>(
            "Criptomonedas",
            "Deuda",
            "Musica",
            "Alcohol",
            "Comida",
            "Transporte",
            "Prestamo",
            "Intereses inv",
            "Gastos Varios",
            "Trabajo",
            "Celular",
            "Salud",
            "Universidad",
            "Casa",
        )

        val types = listOf<String>(
            "Transferencia",
            "Gasto",
            "Ingreso",
        )

        val sources = listOf<String>(
            "Colchon",
            "Caja",
            "Bancolombia",
            "Nequi",
            "Daviplata",
            "Banco agrario",
            "RappyPay"
        )

        setAdapterToAutoCompleteTextView(actvCategories, categories)
        setAdaptersToSpinner(spinnerTypes, types)
        setAdaptersToSpinner(sppinerSources, sources)
        setAdaptersToSpinner(spinnerDestinations, sources)

        etConcept.requestFocus()
        actvCategories.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                actvCategories.showDropDown()
            }
        }

    }

    fun setAdaptersToSpinner(spinner: Spinner, data: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    fun setAdapterToAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, data: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, data)
        autoCompleteTextView.setAdapter(adapter)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spNewTranscType -> {

                val llSource = findViewById<LinearLayout>(R.id.llNewTranscSource)
                val llDestination = findViewById<LinearLayout>(R.id.llNewTranscDestination)

                when (position) {
                    0 -> {
                        llSource.visibility = View.VISIBLE
                        llDestination.visibility = View.VISIBLE
                    }
                    1 -> {
                        llSource.visibility = View.VISIBLE
                        llDestination.visibility = View.GONE
                    }
                    2 -> {
                        llSource.visibility = View.GONE
                        llDestination.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}