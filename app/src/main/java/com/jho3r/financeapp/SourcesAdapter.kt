package com.jho3r.financeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jho3r.financeapp.models.Account

class SourcesAdapter(private val dataSet: List<Account>) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.source_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameSource: TextView = itemView.findViewById(R.id.tvSourceRowName)
        private val valueSource: TextView = itemView.findViewById(R.id.tvSourceRowValue)

        fun bind(item: Account) {
            nameSource.text = item.name
            valueSource.text = item.balance
        }
    }
}