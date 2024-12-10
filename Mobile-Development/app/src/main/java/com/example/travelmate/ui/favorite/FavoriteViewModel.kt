package com.example.travelmate.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.local.database.FavoriteDatabase
import com.example.travelmate.data.local.entity.FavoriteEntity
import com.example.travelmate.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {

    fun insertFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.insertFavorite(favorite)
        }
    }

    fun deleteFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.deleteFavorite(favorite)
        }
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> {
        return repository.getAllFavorites()
    }

    fun isFavorite(placeId: Int): LiveData<Boolean> {
        return repository.isFavorite(placeId)
    }
}