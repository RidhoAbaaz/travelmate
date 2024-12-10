package com.example.travelmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_places")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val city: String,
    val address: String,
    val category: String,
    val price: String,
    val rating: Float
)