package com.yanfiq.streamfusion.ui.youtube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yanfiq.streamfusion.R
import com.yanfiq.streamfusion.data.response.youtube.Video
import com.yanfiq.streamfusion.data.response.youtube.VideoItem

class VideoAdapter(private var videos: List<Video>) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_creator)
        val thumbnail: ImageView = itemView.findViewById(R.id.item_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.title.text = video.title
        holder.description.text = video.description
        Glide.with(holder.thumbnail.context).load(video.thumbnailUrl).into(holder.thumbnail)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(video)
        }
    }

    override fun getItemCount() = videos.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Video)
    }
}
