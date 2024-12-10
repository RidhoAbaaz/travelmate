package com.example.travelmate.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmate.data.response.DataItem
import com.example.travelmate.databinding.ItemRecommendationBinding

class ResultAdapter(
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    private val itemList = mutableListOf<DataItem>()

    inner class ResultViewHolder(private val binding: ItemRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItem) {
            binding.textViewName.text = item.name
            binding.textViewAddress.text = item.address
            binding.textViewCategory.text = item.category
            binding.textViewCity.text = item.city
            binding.textViewPrice.text = "Rp. " + item.price
            binding.textViewRating.text = item.rating.toString()

            // Klik item
            binding.root.setOnClickListener {
                onItemClicked(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemRecommendationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun submitList(newList: List<DataItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }
}
