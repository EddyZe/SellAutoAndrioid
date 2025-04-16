package com.example.sellauto.fragments

import android.app.Notification
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sellauto.clients.sellauto.SellAutoClient
import com.example.sellauto.databinding.AdsFragmentBinding
import com.example.sellauto.holders.AdAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdsFragment : Fragment() {

    private var _binding: AdsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw IllegalArgumentException("Binding must not be null")
    private lateinit var adAdapter: AdAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                val ads = withContext(Dispatchers.IO) {
                    val sellAutoClient = SellAutoClient()
                    sellAutoClient.getAds()
                }

                val recyclerView: RecyclerView = binding.adsList
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                adAdapter = AdAdapter(requireContext(), ads)
                recyclerView.adapter = adAdapter

                binding.adsList.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                binding.errorMessage.text = "Ошибка загрузки данных"
                binding.errorMessage.visibility = View.VISIBLE
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}