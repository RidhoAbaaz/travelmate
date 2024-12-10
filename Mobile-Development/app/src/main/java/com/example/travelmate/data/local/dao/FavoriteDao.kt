package com.example.travelmate.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.travelmate.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorite_places")
    fun getAllFavorites(): LiveData<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT * FROM favorite_places WHERE id = :placeId)")
    fun isFavorite(placeId: Int): LiveData<Boolean>
}