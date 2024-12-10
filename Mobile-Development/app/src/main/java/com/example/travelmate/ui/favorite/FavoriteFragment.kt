package com.example.travelmate.ui.favorite

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelmate.R
import com.example.travelmate.data.local.database.FavoriteDatabase
import com.example.travelmate.data.repository.FavoriteRepository
import com.example.travelmate.databinding.FragmentFavoriteBinding


class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        // Setup ViewModel
        val application = requireNotNull(this.activity).application
        val repository = FavoriteRepository(FavoriteDatabase.getDatabase(application).favoriteDao())
        val viewModelFactory = FavoriteViewModelFactory(repository)
        favoriteViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteViewModel::class.java]

        adapter = FavoriteAdapter { favoriteItem ->
            // Navigasi ke DetailFragment
            val action = FavoriteFragmentDirections.actionFavoriteToDetail(favoriteItem.id)
            findNavController().navigate(action)
        }

        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.adapter = adapter

        favoriteViewModel.getAllFavorites().observe(viewLifecycleOwner) { favoriteList ->
            adapter.submitList(favoriteList)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}