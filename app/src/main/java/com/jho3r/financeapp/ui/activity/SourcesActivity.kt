package com.jho3r.financeapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.SourcesAdapter
import com.jho3r.financeapp.R
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService
import com.jho3r.financeapp.ui.fragment.ModifyAccountFragment
import com.jho3r.financeapp.utils.Constants
import com.jho3r.financeapp.utils.UserMessagesHandler

private const val USERID_KEY = "userId"
private const val TAG = "MyApp.Sources"

class SourcesActivity : AppCompatActivity(), OnClickListener {

    private lateinit var userId : String
    private var currentResult = Constants.RESULT_CANCELED

    private lateinit var rvSources : RecyclerView
    private lateinit var fabSourcesAdd : FloatingActionButton

    private lateinit var firestoreService : FirestoreService
    private lateinit var messagesHandler : UserMessagesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)

        userId = intent.getStringExtra(USERID_KEY) ?: ""

        // change title bar
        supportActionBar?.title = getString(R.string.config_title)

        rvSources = findViewById(R.id.rvSources)
        fabSourcesAdd = findViewById(R.id.fabSourcesAdd)
        fabSourcesAdd.setOnClickListener(this)

        firestoreService = FirestoreService(Firebase.firestore)
        messagesHandler = UserMessagesHandler(this)

        // manage recycler view
        rvSources.layoutManager = LinearLayoutManager(this)

        tryGetData()

    }

    private fun tryGetData() {
        if (userId.isNotEmpty()) {
            firestoreService.getSources(
                userId = userId,
                callback = object : Callback<Map<String, Account>> {
                    override fun onSuccess(response: Map<String, Account>?) {
                        if (response != null) {
                            var listSources = mutableListOf<Account>()
                            response.values.forEach {
                                listSources.add(it)
                            }
                            rvSources.adapter = SourcesAdapter(listSources, ::onAccountClick)
                        } else {
                            messagesHandler
                                .showToastErrorMessage("Error obteniendo datos de usuario", null)
                        }
                    }

                    override fun onFailure(exception: Exception) {
                        messagesHandler
                            .showToastErrorMessage(exception.message?:"Error obteniendo datos de usuario", null)
                    }

                }
            )
        } else {
            messagesHandler
                .showToastErrorMessage("Error obteniendo datos de usuario", null)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabSourcesAdd -> startAddSourceActivity()
        }
    }

    private fun startAddSourceActivity() {
        // start add source activity for result
        val intent = Intent(this, AddSourceActivity::class.java)
        intent.putExtra(USERID_KEY, userId)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(TAG, "Result: $result")
        if (result.resultCode == Constants.RESULT_DATABASE_CHANGED) {
            currentResult = Constants.RESULT_DATABASE_CHANGED
            tryGetData()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            val intent = Intent()
            intent.putExtra(USERID_KEY, userId)
            setResult(currentResult, intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun onAccountClick(account: Account) {
        val fm = supportFragmentManager
        val dialog = ModifyAccountFragment(account, ::onDialogPositiveClick)
        dialog.show(fm, "fragment_edit_name")
    }

    fun onDialogPositiveClick(account: Account) {
        firestoreService.updateAccount(
            userId = userId,
            account = account,
            callback = object : Callback<Void> {
                override fun onSuccess(response: Void?) {
                    currentResult = Constants.RESULT_DATABASE_CHANGED
                    tryGetData()
                    messagesHandler
                        .showToastSuccessMessage("Cuenta actualizada")
                }

                override fun onFailure(exception: Exception) {
                    messagesHandler
                        .showToastErrorMessage(exception.message?:"Error actualizando cuenta", null)
                }
            }
        )
    }

}