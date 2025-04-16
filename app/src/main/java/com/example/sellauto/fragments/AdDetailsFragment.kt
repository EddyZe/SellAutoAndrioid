package com.example.sellauto.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sellauto.clients.sellauto.SellAutoClient
import com.example.sellauto.clients.sellauto.payloads.ads.AdDetailsPayload
import com.example.sellauto.clients.sellauto.payloads.ads.CarDetailsDto
import com.example.sellauto.databinding.AdDetailsFragmentBinding
import com.example.sellauto.holders.AdDerailsAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.NumberFormat


class AdDetailsFragment : Fragment() {
    private var _binding: AdDetailsFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("AdDetailsFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sellAutoClient = SellAutoClient()
        val adId = requireArguments().getLong("ad_id")

        lifecycleScope.launch {
            try {
                val ad = sellAutoClient.getAd(adId)
                val car = ad.car
                val photos: List<InputStream> = car.photos.map {
                    val response = sellAutoClient.getPhoto(it.photoId)
                    (if (response.isSuccessful) {
                        response.body()?.byteStream()
                    } else {
                        null
                    })!!
                }

                binding.photoViewPager.adapter = AdDerailsAdapter(photos)
                generateAdTitle(ad)
                binding.adDescription.text = generateText(ad)
                val historyPrices = binding.historyPricesChart
                val prices = ad.prices

                if (prices.size < 2) {
                    historyPrices.visibility = View.GONE
                    binding.priceNotEdited.visibility = View.VISIBLE
                    return@launch
                }

                val entries: MutableList<Entry> = prices
                    .mapIndexed() { index, price ->
                        Entry(index.toFloat(), price.price.toFloat())
                    }
                    .toMutableList()

                entries.add(0, Entry(0f, 0f))
                val lineDataSet = LineDataSet(entries, "Цены")
                lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                val lineData = LineData(lineDataSet)
                historyPrices.data = lineData
                historyPrices.invalidate()
            } catch (e: Exception) {
                binding.adDetails.visibility = View.GONE
                binding.errorMessage.visibility = View.VISIBLE
                e.printStackTrace()
            }
        }
    }

    private fun generateAdTitle(ad: AdDetailsPayload) {
        "${ad.car.brand.title} ${ad.car.model.title} ${NumberFormat.getInstance().format(ad.prices[ad.prices.size - 1].price.toInt())} ₽".also { binding.adTitle.text = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateText(ad: AdDetailsPayload) : String = """
        Пробег: ${NumberFormat.getInstance().format(ad.car.mileage)} км
        Год выпуска: ${ad.car.year}
        Тип двигателя: ${ad.car.engineType.type}
        Тип трансмиссии: ${ad.car.transmissionType.type}
        Тип кузова: ${ad.car.bodyType.type}
        Цвет: ${ad.car.color.title}
        VIN: ${ad.car.vin}
        
        Продавец: ${ad.user.firstName} ${ad.user.lastName}
        
    """.trimIndent()
}