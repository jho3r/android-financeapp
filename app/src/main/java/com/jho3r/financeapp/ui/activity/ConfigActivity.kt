package com.jho3r.financeapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jho3r.financeapp.ConfigAdapter
import com.jho3r.financeapp.R

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        // change title bar
        supportActionBar?.title = getString(R.string.config_title)

        val arrayString = listOf(
            mapOf("name" to "Nequi", "value" to "3'600.000"),
            mapOf("name" to "Banco1", "value" to "4'600.000"),
            mapOf("name" to "Banco2", "value" to "5'600.000"),
            mapOf("name" to "Banco3", "value" to "6'600.000"),
        )

        // manage recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.rvConfigSources)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ConfigAdapter(arrayString)

    }
}