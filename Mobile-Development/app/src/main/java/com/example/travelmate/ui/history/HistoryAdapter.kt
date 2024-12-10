package com.example.travelmate.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmate.R
import com.example.travelmate.data.response.ProgressItem


class HistoryAdapter (
    private val history: List<ProgressItem>,
//    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewNameHistory)
        val textViewAddress: TextView = view.findViewById(R.id.textViewAddressHistory)
        val textViewCategory: TextView = view.findViewById(R.id.textViewCategoryHistory)
        val textViewCity: TextView = view.findViewById(R.id.textViewCityHistory)
        val textViewRating: TextView = view.findViewById(R.id.textViewRatingHistory)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = history[position]
        holder.textViewName.text = history.name
        holder.textViewAddress.text = history.address
        holder.textViewCategory.text = history.category
        holder.textViewCity.text = history.city
        holder.textViewRating.text = "Rating: ${history.rating}"
        holder.textViewDate.text = history.date

//        holder.itemView.setOnClickListener{
//            onItemClick(history.id)
//        }
    }

    override fun getItemCount() = history.size
}