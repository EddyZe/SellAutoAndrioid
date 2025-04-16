package com.example.sellauto.holders

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sellauto.R
import com.example.sellauto.clients.sellauto.SellAutoClient
import com.example.sellauto.clients.sellauto.payloads.ads.AdDetailsPayload
import com.example.sellauto.clients.sellauto.payloads.ads.CarDetailsDto
import com.example.sellauto.fragments.AdDetailsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat


class AdAdapter(private val context: Context, private val adList: List<AdDetailsPayload>) :
    RecyclerView.Adapter<AdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.ad_item, parent, false)
        return AdViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = adList[position]
        val car = ad.car
        adTitle(car, holder)
        addDescription(car, holder)
        adPrice(holder, ad)

        val firstPhotoId = car.photos.firstOrNull()?.photoId ?: return

        holder.itemView.post {
            CoroutineScope(Dispatchers.IO).launch {
                val response = SellAutoClient().getPhoto(firstPhotoId)

                if (response.isSuccessful) {
                    val inputStream = response.body()?.byteStream()
                    inputStream?.let {
                        val bytes = it.readBytes()

                        withContext(Dispatchers.Main) {
                            Glide.with(holder.itemView.context)
                                .asBitmap()
                                .load(bytes)
                                .into(holder.adImage)
                        }
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val adId = ad.adId
            val bundle = bundleOf("ad_id" to adId)
            it.findNavController().navigate(
                R.id.adDetailsFragment,
                bundle
            )
        }
    }

    private fun adPrice(
        holder: AdViewHolder,
        ad: AdDetailsPayload
    ) {
        holder.adPrice.text = if (ad.prices.isEmpty()) {
            "0 ₽"
        } else {
            val price = ad.prices[ad.prices.size - 1].price.toInt()
            val priceString: String = NumberFormat.getInstance().format(price)
            "$priceString ₽"
        }
    }

    private fun adTitle(
        car: CarDetailsDto,
        holder: AdViewHolder
    ) {
        "${car.brand.title} ${car.model.title}".also { holder.adTitle.text = it }
    }

    private fun addDescription(
        car: CarDetailsDto,
        holder: AdViewHolder
    ) {
        """
                    ${NumberFormat.getInstance().format(car.mileage)} км ${car.year} 
                    ${car.transmissionType.type} ${car.bodyType.type}
                """.trimIndent().also { holder.adDescription.text = it }
    }

    override fun getItemCount(): Int {
        return adList.size
    }
}