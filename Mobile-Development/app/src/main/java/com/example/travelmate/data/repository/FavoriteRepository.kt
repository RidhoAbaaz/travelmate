package com.example.travelmate.data.repository

import androidx.lifecycle.LiveData
import com.example.travelmate.data.local.dao.FavoriteDao
import com.example.travelmate.data.local.entity.FavoriteEntity

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    suspend fun insertFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(favorite: FavoriteEntity) {
        favoriteDao.deleteFavorite(favorite)
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    fun isFavorite(placeId: Int): LiveData<Boolean> {
        return favoriteDao.isFavorite(placeId)
    }
}
