package com.example.travelmate.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmate.data.local.entity.FavoriteEntity
import com.example.travelmate.databinding.ItemFavoriteBinding

class FavoriteAdapter(
    private val onItemClick: (FavoriteEntity) -> Unit
) : ListAdapter<FavoriteEntity, FavoriteAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteItem = getItem(position)
        holder.bind(favoriteItem)
    }

    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteItem: FavoriteEntity) {
            binding.textViewName.text = favoriteItem.name
            binding.textViewAddress.text = favoriteItem.address
            binding.textViewCategory.text = favoriteItem.category
            binding.textViewCity.text = favoriteItem.city
            binding.textViewPrice.text = favoriteItem.price
            binding.textViewRating.text = favoriteItem.rating.toString()

            // Klik item untuk navigasi
            binding.root.setOnClickListener {
                onItemClick(favoriteItem)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEntity>() {
            override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
