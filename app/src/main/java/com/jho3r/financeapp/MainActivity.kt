package com.jho3r.financeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jho3r.financeapp.ui.activity.ConfigActivity
import com.jho3r.financeapp.ui.activity.NewTransactionActivity

class MainActivity : AppCompatActivity(), OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabMain: FloatingActionButton = findViewById(R.id.fabMain)

        fabMain.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // go to config activity
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabMain -> {
                val intent = Intent(this, NewTransactionActivity::class.java)
                startActivity(intent)
            }
        }
    }
}