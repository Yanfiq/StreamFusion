package com.yanfiq.streamfusion.ui.search.spotify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yanfiq.streamfusion.R
import com.yanfiq.youcloudify.data.response.spotify.Track

class TrackAdapter(private var tracks: List<Track>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val artist: TextView = itemView.findViewById(R.id.item_creator)
        val albumArt: ImageView = itemView.findViewById(R.id.item_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.title.text = track.name
        holder.artist.text = track.artists.joinToString(", ") { it.name }
        Glide.with(holder.albumArt.context).load(track.album.images.firstOrNull()?.url).into(holder.albumArt)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(track)
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
