package com.jho3r.financeapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.R
import com.jho3r.financeapp.SourcesAdapter
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService
import com.jho3r.financeapp.utils.Constants
import com.jho3r.financeapp.utils.UserMessagesHandler

private const val USERID_KEY = "userId"
private const val TAG = "MyApp.AddSource"

class AddSourceActivity : AppCompatActivity(), OnClickListener {

    private lateinit var userId: String

    private lateinit var btnAddSourceSave: Button
    private lateinit var etAddSourceDescription: EditText
    private lateinit var etAddSourceBalance: EditText
    private lateinit var etAddSourceName: EditText

    private lateinit var firestoreService: FirestoreService
    private lateinit var messagesHandler: UserMessagesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_source)

        userId = intent.getStringExtra(USERID_KEY) ?: ""

        // change title bar
        supportActionBar?.title = getString(R.string.addsource_title)

        etAddSourceDescription = findViewById(R.id.etAddSourceDescription)
        etAddSourceBalance = findViewById(R.id.etAddSourceBalance)
        etAddSourceName = findViewById(R.id.etAddSourceName)
        btnAddSourceSave = findViewById(R.id.btnAddSourceSave)

        btnAddSourceSave.setOnClickListener(this)

        firestoreService = FirestoreService(Firebase.firestore)
        messagesHandler = UserMessagesHandler(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddSourceSave -> validateAccount(p0)
        }
    }

    private fun validateAccount(view: View) {
        view.isEnabled = false

        val sourceName = etAddSourceName.text.toString().trim()
        val sourceNameTrim = sourceName.trim()
        if (sourceNameTrim.isEmpty()) {
            messagesHandler.showToastErrorMessage("El nombre no puede estar vacio", view)
            return
        }

        val sourceBalance = etAddSourceBalance.text.toString()
        val sourceBalanceTrim = sourceBalance.trim()
        if (sourceBalanceTrim.isEmpty()) {
            messagesHandler.showToastErrorMessage("El balance no puede estar vacio", view)
            return
        }

        val sourceDescription = etAddSourceDescription.text.toString()
        val sourceDescriptionTrim = sourceDescription.trim()
        if (sourceDescriptionTrim.isEmpty()) {
            messagesHandler.showToastErrorMessage("La descripcion no puede estar vacia", view)
            return
        }

        val sourceId = sourceName.lowercase().replace(" ", "-")

        val source = Account(
            id = sourceId,
            name = sourceName,
            balance = sourceBalance,
            description = sourceDescription
        )

        firestoreService.getSources(
            userId = userId,
            callback = object : Callback<Map<String, Account>>{
                override fun onSuccess(response: Map<String, Account>?) {
                    if (response != null) {
                        val ids = response.values.map { it.getId() }
                        if (ids.contains(sourceId)) {
                            messagesHandler.showToastErrorMessage("La fuente ya existe", view)
                        } else {
                            tryAddSource(source, view)
                        }
                    } else {
                        messagesHandler
                            .showToastErrorMessage("Error obteniendo cuentas", null)
                    }

                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error getting sources", exception)
                    messagesHandler.showToastErrorMessage(exception.message.toString(), view)
                }

            }
        )
    }

    private fun tryAddSource(source: Account, view: View) {
        firestoreService.addAccount(
            userId = userId,
            account = source,
            callback = object : Callback<Void>{
                override fun onSuccess(response: Void?) {
                    view.isEnabled = true
                    Log.d(TAG, "Source added")
                    messagesHandler.showToastSuccessMessage("Fuente agregada")
                    returnToSourcesWithChange(view)
                }

                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error adding source", exception)
                    messagesHandler.showToastErrorMessage(exception.message?: "Error agregando cuenta", view)
                }

            }
        )
    }

    private fun returnToSourcesWithChange(view: View) {
        view.isEnabled = true
        val intent = Intent()
        intent.putExtra(USERID_KEY, userId)
        setResult(Constants.RESULT_DATABASE_CHANGED, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            val intent = Intent()
            intent.putExtra(USERID_KEY, userId)
            setResult(Constants.RESULT_CANCELED, intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}