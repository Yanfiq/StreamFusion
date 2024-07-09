package com.yanfiq.streamfusion.ui.search.soundcloud

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.response.soundcloud.Track

class TrackAdapter(private var tracks: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val artist: TextView = itemView.findViewById(R.id.item_creator)
        val artwork: ImageView = itemView.findViewById(R.id.item_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.title.text = track.title
        holder.artist.text = track.user
        Glide.with(holder.artwork.context).load(track.artwork_url).into(holder.artwork)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(track)
        }
    }

    override fun getItemCount() = tracks.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Track)
    }
}
