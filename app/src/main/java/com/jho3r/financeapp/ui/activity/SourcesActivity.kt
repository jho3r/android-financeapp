package com.jho3r.financeapp.ui.activity

import android.app.TaskStackBuilder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.SourcesAdapter
import com.jho3r.financeapp.R
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService
import com.jho3r.financeapp.utils.UserMessagesHandler

private const val USERID_KEY = "userId"

class SourcesActivity : AppCompatActivity() {

    private lateinit var userId : String

    private lateinit var rvSources : RecyclerView

    private lateinit var firestoreService : FirestoreService
    private lateinit var messagesHandler : UserMessagesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources)

        userId = intent.getStringExtra(USERID_KEY) ?: ""

        // change title bar
        supportActionBar?.title = getString(R.string.config_title)

        rvSources = findViewById(R.id.rvSources)

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
                            rvSources.adapter = SourcesAdapter(listSources)
                        } else {
                            messagesHandler
                                .showToastErrorMessage("Error obteniendo datos de usuario")
                        }
                    }

                    override fun onFailure(exception: Exception) {
                        messagesHandler
                            .showToastErrorMessage(exception.message?:"Error obteniendo datos de usuario")
                    }

                }
            )
        } else {
            messagesHandler
                .showToastErrorMessage("Error obteniendo datos de usuario")
        }
    }

}