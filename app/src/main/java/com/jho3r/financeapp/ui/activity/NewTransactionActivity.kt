package com.jho3r.financeapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.R
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.models.Transaction
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService
import com.jho3r.financeapp.models.User
import com.jho3r.financeapp.utils.Constants
import com.jho3r.financeapp.utils.UserMessagesHandler

private const val USERID_KEY = "userId"
private const val TAG = "MyApp.NewTransc"

class NewTransactionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, OnClickListener {

    private lateinit var actvCategories : AutoCompleteTextView
    private lateinit var etAmount : EditText
    private lateinit var etConcept : EditText
    private lateinit var spinnerTypes : Spinner
    private lateinit var spinnerSources : Spinner
    private lateinit var spinnerDestinations : Spinner
    private lateinit var fabNewTransc : FloatingActionButton
    private lateinit var cbIsPending : CheckBox

    private lateinit var userId: String
    private lateinit var type : String
    private lateinit var categories : MutableList<String>

    private lateinit var firestoreService: FirestoreService
    private lateinit var messagesHandler: UserMessagesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)

        userId = intent.getStringExtra(USERID_KEY) ?: ""
        Log.d(TAG, "UserId: $userId")

        supportActionBar?.title = getString(R.string.newtransc_title)

        etConcept = findViewById(R.id.etNewTranscConcept)
        etAmount = findViewById(R.id.etNewTranscAmount)
        actvCategories = findViewById(R.id.actvNewTranscCategory)
        spinnerTypes = findViewById(R.id.spNewTranscType)
        spinnerSources = findViewById(R.id.spNewTranscSource)
        spinnerDestinations = findViewById(R.id.spNewTranscDestination)
        fabNewTransc = findViewById(R.id.fabNewTransc)
        cbIsPending = findViewById(R.id.cbNewTranscIsPending)

        setAdaptersToSpinner(spinnerTypes, resources.getStringArray(R.array.newtransc_array_type).toList())
        etConcept.requestFocus()
        actvCategories.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                actvCategories.showDropDown()
            }
        }
        fabNewTransc.setOnClickListener(this)

        firestoreService = FirestoreService(Firebase.firestore)
        messagesHandler = UserMessagesHandler(this)

        tryLoadSpinnersData()
    }



    private fun tryLoadSpinnersData() {
        firestoreService.getUser(
            userId = userId,
            callback = object : Callback<User>{
                override fun onSuccess(response: User?) {
                    if (response != null) {
                        val user = response
                        categories = user.getCategories()
                        setAdapterToAutoCompleteTextView(actvCategories, categories)
                    }
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error al cargar datos usuario", exception)
                    messagesHandler.showToastErrorMessage("Error al obtener datos de usuario")
                }

            }
        )
        firestoreService.getSources(
            userId = userId,
            callback = object : Callback<Map<String,Account>>{
                override fun onSuccess(response: Map<String,Account>?) {
                    if (response != null) {
                        val sources : List<String> = response.values.map { it.name }
                        setAdaptersToSpinner(spinnerSources, sources)
                        setAdaptersToSpinner(spinnerDestinations, sources)
                    }
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error al cargar cuentas usuario", exception)
                    messagesHandler.showToastErrorMessage("Error al obtener cuentas de usuario")
                }

            }
        )

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
                        type = Constants.TRANSACTION_TYPE_TRANSFER
                    }
                    1 -> {
                        llSource.visibility = View.VISIBLE
                        llDestination.visibility = View.GONE
                        type = Constants.TRANSACTION_TYPE_EXPENSE
                    }
                    2 -> {
                        llSource.visibility = View.GONE
                        llDestination.visibility = View.VISIBLE
                        type = Constants.TRANSACTION_TYPE_INCOME
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.fabNewTransc -> tryAddNewTransaction()
        }
    }

    private fun tryAddNewTransaction() {
        val concept = etConcept.text.toString()
        val conceptTrimmed = concept.trim()
        if (conceptTrimmed.isEmpty()) {
            messagesHandler.showToastErrorMessage("El concepto no puede estar vacío")
            return
        }

        val amount = etAmount.text.toString()
        // remove white spaces
        val amountTrimmed = amount.trim()
        if (amountTrimmed.isEmpty()) {
            messagesHandler.showToastErrorMessage("El monto no puede estar vacío")
            return
        }

        val category = actvCategories.text.toString()
        val categoryTrimmed = category.trim()
        if (categoryTrimmed.isEmpty()) {
            messagesHandler.showToastErrorMessage("La categoría no puede estar vacía")
            return
        }

        if (!categories.contains(category)) {
            categories.add(category)
            firestoreService.updateField("categories", categories, userId,
            callback = object : Callback<Void>{
                override fun onSuccess(response: Void?) {
                    Log.d(TAG, "Categories updated")
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error updating categories", exception)
                }

            })
        }

        val source = if (type == Constants.TRANSACTION_TYPE_TRANSFER || type == Constants.TRANSACTION_TYPE_EXPENSE) {
            spinnerSources.selectedItem.toString().lowercase()
        } else {
            null
        }

        val destination = if (type == Constants.TRANSACTION_TYPE_TRANSFER || type == Constants.TRANSACTION_TYPE_INCOME) {
            spinnerDestinations.selectedItem.toString().lowercase()
        } else {
            null
        }

        val isPending = cbIsPending.isChecked

        val error : Exception? = when (type) {
            Constants.TRANSACTION_TYPE_INCOME -> tryUpdateIncome(amount, destination)
            Constants.TRANSACTION_TYPE_EXPENSE -> tryUpdateExpense(amount, source)
            Constants.TRANSACTION_TYPE_TRANSFER -> tryUpdateTransfer(amount, source, destination)
            else -> Exception("Invalid transaction type")
        }

        if (error != null) {
            messagesHandler.showToastErrorMessage(error.message ?: "Error al realizar la transacción")
            return
        }

        val transaction = Transaction(
            userId = userId,
            concept = conceptTrimmed,
            category = categoryTrimmed,
            type = type,
            amount = amountTrimmed.toDouble(),
            source = source,
            destination = destination,
            isPending = isPending
        )

        firestoreService.addTransaction(
            transaction = transaction,
            callback = object : Callback<Void>{
                override fun onSuccess(response: Void?) {
                    messagesHandler.showToastSuccessMessage("Transacción agregada")
                    finish()
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error adding transaction", exception)
                    messagesHandler.showToastErrorMessage("Error al agregar la transacción")
                }

            }
        )

    }

    private fun tryUpdateTransfer(amount: String, source: String?, destination: String?): Exception? {
        if (source == null || destination == null) {
            return Exception("Debe seleccionar una cuenta de origen y destino")
        }
        Log.d(TAG, "tryUpdateTransfer")
        var error : Exception? = null
        error = tryUpdateIncome(amount, destination)
        if (error == null) {
            error = tryUpdateExpense(amount, source)
        }
        return error
    }

    private fun tryUpdateExpense(amount: String, source: String?): Exception? {
        if (source == null) {
            return Exception("Debe seleccionar una cuenta de origen")
        }
        var error : Exception? = null
        firestoreService.updateBalance(
            amount = amount.toDouble() * -1,
            userId = userId,
            accountId = source,
            callback = object : Callback<Void>{
                override fun onSuccess(response: Void?) {
                    Log.d(TAG, "Expense updated")
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error updating expense", exception)
                    error = exception
                }

            }
        )
        return error
    }

    private fun tryUpdateIncome(amount: String, destination: String?): Exception? {
        if (destination == null) {
            return Exception("Debe seleccionar una cuenta de destino")
        }
        var error : Exception? = null
        firestoreService.updateBalance(
            amount = amount.toDouble(),
            userId = userId,
            accountId = destination,
            callback = object : Callback<Void>{
                override fun onSuccess(response: Void?) {
                    Log.d(TAG, "Income updated")
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error updating income", exception)
                    error = exception
                }

            }
        )
        return error
    }
}