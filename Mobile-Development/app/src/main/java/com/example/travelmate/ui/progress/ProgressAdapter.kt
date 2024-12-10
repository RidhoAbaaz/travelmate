package com.example.travelmate.ui.progress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmate.R
import com.example.travelmate.data.response.ProgressItem

class ProgressAdapter(
    val places:  MutableList<ProgressItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ProgressAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewNameProgress: TextView = view.findViewById(R.id.textViewNameProgress)
        val textViewAddressProgress: TextView = view.findViewById(R.id.textViewAddressProgress)
        val textViewCategoryProgress: TextView = view.findViewById(R.id.textViewCategoryProgress)
        val textViewCityProgress: TextView = view.findViewById(R.id.textViewCityProgress)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_progress, parent, false)

        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.textViewNameProgress.text = place.name
        holder.textViewAddressProgress.text = place.address
        holder.textViewCategoryProgress.text = place.category
        holder.textViewCityProgress.text = place.city
        holder.textViewDate.text= place.date

        holder.deleteButton.setOnClickListener(){
            onItemClick(place.id)
        }
    }

    override fun getItemCount() = places.size

    fun removeItem(position: Int) {
        places.removeAt(position)
        notifyItemRemoved(position)
    }
}


