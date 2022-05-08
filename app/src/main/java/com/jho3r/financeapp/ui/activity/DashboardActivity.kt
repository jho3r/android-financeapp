package com.jho3r.financeapp.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jho3r.financeapp.MainActivity
import com.jho3r.financeapp.R
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.network.AuthService
import com.jho3r.financeapp.network.Callback
import com.jho3r.financeapp.network.FirestoreService
import com.jho3r.financeapp.utils.Constants
import com.jho3r.financeapp.utils.UserMessagesHandler

private const val USERID_KEY = "userId"
private const val TAG = "MyApp.Dashboard"
private const val SP_EMAIL_KEY = "email"
private const val SP_PASS_KEY = "password"

class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fabDashboard: FloatingActionButton
    private lateinit var tvDashboardAvailable: TextView
    private lateinit var cvDashboardGraphs: CardView
    private lateinit var llDashboardHeader: LinearLayout

    private lateinit var userId: String

    private lateinit var firestoreService: FirestoreService
    private lateinit var authService: AuthService
    private lateinit var messagesHandler: UserMessagesHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        // Get the Intent that started this activity and extract the string
        userId = intent.getStringExtra(USERID_KEY) ?: ""
        Log.d(TAG, "UserId: $userId")

        // Define the views and set the onClickListener
        tvDashboardAvailable = findViewById(R.id.tvDashboardAvailable)

        llDashboardHeader = findViewById(R.id.llDashboardHeader)
        llDashboardHeader.setOnClickListener(this)

        cvDashboardGraphs = findViewById(R.id.cvDashboardGraphs)
        cvDashboardGraphs.setOnClickListener(this)

        fabDashboard = findViewById(R.id.fabDashboard)
        fabDashboard.setOnClickListener(this)

        firestoreService = FirestoreService(Firebase.firestore)
        authService = AuthService(Firebase.auth)
        messagesHandler = UserMessagesHandler(this)
        tryGetData()
    }

    override fun onBackPressed() {
        // show dialog to confirm exit
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dashboard_title_dialogExit))
        builder.setMessage(getString(R.string.dashboard_text_dialogExit))
        builder.setPositiveButton(getString(R.string.dashboard_text_dialogExit_yes)) { _, _ ->
            finishAndRemoveTask()
        }
        builder.setNegativeButton(getString(R.string.dashboard_text_dialogExit_no)) { _, _ ->
            // do nothing
        }
        builder.show()
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
        R.id.action_signout -> {
            // go to config activity
            signout()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun signout() {
        val sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_name), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(SP_EMAIL_KEY, null)
        editor.putString(SP_PASS_KEY, null)
        editor.apply()
        authService.logout()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabDashboard -> {
                startNewTransactionActivity()
            }
            R.id.cvDashboardGraphs -> {
                startGraphsActivity()
            }
            R.id.llDashboardHeader -> {
                startSourcesActivity()
            }
        }
    }

    private fun startGraphsActivity() {
        val intent = Intent(this, GraphsActivity::class.java)
        intent.putExtra(USERID_KEY, userId)
        startActivity(intent)
    }

    private fun startNewTransactionActivity() {
        val intent = Intent(this, NewTransactionActivity::class.java)
        intent.putExtra(USERID_KEY, userId)
        resultLauncher.launch(intent)
    }

    private fun startSourcesActivity() {
        val intent = Intent(this, SourcesActivity::class.java)
        intent.putExtra(USERID_KEY, userId)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(TAG, "Result: $result")
        if (result.resultCode == Constants.RESULT_DATABASE_CHANGED) {
            tryGetData()
            trySaveBalanceAnalytics()
        }
    }

    private fun trySaveBalanceAnalytics() {
        Log.d(TAG, "trySaveBalanceAnalytics")
    }


}