package com.example.sellauto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sellauto.clients.sellauto.SellAutoClient
import com.example.sellauto.databinding.MyAdsFragmentBinding
import com.example.sellauto.holders.AdAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAdsFragment: Fragment() {
    private var _binding: MyAdsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw IllegalArgumentException("Binding must not be null")
    private lateinit var adAdapter: AdAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyAdsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                val ads = withContext(Dispatchers.IO) {
                    val sellAutoClient = SellAutoClient(view.context)
                    sellAutoClient.getMyAds()
                }

                if (ads.isEmpty()) {
                    binding.errorMessage.text = "Здесь пока-что пусто ☺️"
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.adsList.visibility = View.GONE
                    return@launch
                }

                val recyclerView: RecyclerView = binding.adsList
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                adAdapter = AdAdapter(requireContext(), ads)
                recyclerView.adapter = adAdapter
            } catch (e: Exception) {
                e.printStackTrace()
                binding.errorMessage.text = "Ошибка загрузки данных"
                binding.errorMessage.visibility = View.VISIBLE
                binding.adsList.visibility = View.GONE
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}