package com.jho3r.financeapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.R
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService
import com.jho3r.financeapp.utils.Constants
import com.jho3r.financeapp.utils.UserMessagesHandler

private const val USERID_KEY = "userId"
private const val TAG = "MyApp.Dashboard"

class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fabDashboard: FloatingActionButton
    private lateinit var tvDashboardAvailable: TextView

    private lateinit var userId: String

    private lateinit var firestoreService: FirestoreService
    private lateinit var messagesHandler: UserMessagesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        // Get the Intent that started this activity and extract the string
        userId = intent.getStringExtra(USERID_KEY) ?: ""
        Log.d(TAG, "UserId: $userId")

        // Define the views and set the onClickListener
        tvDashboardAvailable = findViewById(R.id.tvDashboardAvailable)

        fabDashboard = findViewById(R.id.fabDashboard)
        fabDashboard.setOnClickListener(this)

        firestoreService = FirestoreService(Firebase.firestore)
        messagesHandler = UserMessagesHandler(this)
        tryGetData()
    }

    private fun tryGetData() {
        if (userId.isNotEmpty()) {
            firestoreService.getSources(
                userId = userId,
                callback = object : Callback<Map<String, Account>>{
                    override fun onSuccess(response: Map<String, Account>?) {
                        if (response != null) {
                            var available : Double = 0.0
                            for (account in response.values) {
                                if (account.isCash()) {
                                    available += account.getBalance().toDouble()
                                }
                            }
                            Log.d(TAG, "Available: $available")
                            tvDashboardAvailable.text = getString(R.string.dashboard_text_cash, available.toString())
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
                .showToastErrorMessage("Error obteniendo datos de usuario porfavor reinicie la aplicación", null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // go to config activity
            startSourcesActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun startSourcesActivity() {
        val intent = Intent(this, SourcesActivity::class.java)
        intent.putExtra(USERID_KEY, userId)
        resultLauncher.launch(intent)
    }

    private fun startNewTransactionActivity() {
        val intent = Intent(this, NewTransactionActivity::class.java)
        intent.putExtra(USERID_KEY, userId)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(TAG, "Result: $result")
        if (result.resultCode == Constants.RESULT_DATABASE_CHANGED) {
            tryGetData()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabDashboard -> {
                startNewTransactionActivity()
            }
        }
    }

}