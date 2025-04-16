package com.example.sellauto.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sellauto.R

class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val adImage: ImageView = itemView.findViewById(R.id.adImage)
    val adTitle: TextView = itemView.findViewById(R.id.adTitle)
    val adDescription: TextView = itemView.findViewById(R.id.adDescription)
    val adPrice: TextView = itemView.findViewById(R.id.adPrice)

}