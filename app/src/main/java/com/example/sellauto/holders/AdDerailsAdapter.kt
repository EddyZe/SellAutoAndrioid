package com.example.sellauto.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sellauto.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class AdDerailsAdapter(
    private val photos: List<InputStream>
) : RecyclerView.Adapter<AdDerailsAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(val imageView: ImageView) :
        RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false) as ImageView
        return PhotoViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.imageView.post {
            CoroutineScope(Dispatchers.IO).launch {
                val inputStream: InputStream = photos[position]
                inputStream.let {
                    val bytes = it.readBytes()

                    withContext(Dispatchers.Main) {
                        Glide.with(holder.itemView.context)
                            .asBitmap()
                            .load(bytes)
                            .into(holder.imageView)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = photos.size
}