package com.jho3r.financeapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jho3r.financeapp.models.Account
import com.jho3r.financeapp.ui.fragment.ModifyAccountFragment

private const val TAG = "MyApp.SourcesAdapter"

class SourcesAdapter(private val dataSet: List<Account>, val onAccountClick: (Account) -> Unit)
    : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.source_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameSource.text = dataSet[position].getName()
        holder.valueSource.text = dataSet[position].getBalance()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameSource: TextView
        val valueSource: TextView
        val cvSourceCard : CardView

        init {
            nameSource = itemView.findViewById(R.id.tvSourceRowName)
            valueSource = itemView.findViewById(R.id.tvSourceRowValue)
            cvSourceCard = itemView.findViewById(R.id.cvSourceRowCard)
            cvSourceCard.setOnClickListener {
                Log.d(TAG, "Clicked on item $adapterPosition")
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = dataSet[position]
                    onAccountClick(item)
                }
            }
        }
    }

}