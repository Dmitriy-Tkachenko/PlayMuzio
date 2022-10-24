package com.tk4dmitriy.playmuzio.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.Track
import com.tk4dmitriy.playmuzio.utils.TAG

private const val SET_COLOR_ITEM_VIEW = "SET_COLOR_ITEM_VIEW"
private const val REMOVE_COLOR_ITEM_VIEW = "REMOVE_COLOR_ITEM_VIEW"

class HomeTrackRecommendationsAdapter: RecyclerView.Adapter<HomeTrackRecommendationsAdapter.ViewHolder>() {
    interface Callback {
        fun touchUp(position: Int)
    }

    private val trackRecommendations: MutableList<Track> = mutableListOf()
    private var selectedTrack = -1
    private lateinit var callback: Callback

    fun attachCallback(callback: Callback) {
        this.callback = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(recommendedTracks: List<Track>) {
        this.trackRecommendations.addAll(recommendedTracks)
        notifyDataSetChanged()
    }

    fun selectNewTrack(position: Int) {
        selectedTrack = position
        notifyItemChanged(selectedTrack, SET_COLOR_ITEM_VIEW)
    }

    fun deselectOldTrack() {
        if (selectedTrack != -1) {
            notifyItemChanged(selectedTrack, REMOVE_COLOR_ITEM_VIEW)
            selectedTrack = -1
        }
    }

    fun deselectTrack(position: Int) {
        notifyItemChanged(position, REMOVE_COLOR_ITEM_VIEW)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_recommendation_item, parent,false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommendedTracks = trackRecommendations[position]
        holder.bind(recommendedTracks)
        if (position == selectedTrack) {
            holder.setColorItemView()
        } else {
            holder.removeColorItemView()
        }
    }

    override fun onBindViewHolder(holder: HomeTrackRecommendationsAdapter.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads)
        else {
            for (payload in payloads) {
                if (payload == SET_COLOR_ITEM_VIEW) holder.setColorItemView()
                else if (payload == REMOVE_COLOR_ITEM_VIEW) holder.removeColorItemView()
            }
        }
    }

    override fun getItemCount() = trackRecommendations.size

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val sivAlbumImage: ShapeableImageView = view.findViewById(R.id.album_image)
        private val tvAlbumName: TextView = view.findViewById(R.id.album_name)
        private val tvArtistName: TextView = view.findViewById(R.id.tv_artist_name)

        init {
            view.setOnClickListener {  }
            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        notifyItemChanged(bindingAdapterPosition, SET_COLOR_ITEM_VIEW)
                    }
                    MotionEvent.ACTION_UP -> {
                        callback.touchUp(bindingAdapterPosition)
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        if (bindingAdapterPosition != selectedTrack) notifyItemChanged(bindingAdapterPosition, REMOVE_COLOR_ITEM_VIEW)
                    }
                }
                v?.onTouchEvent(event) ?: true
            }
        }

        fun bind(model: Track) {
            Glide.with(itemView).load(model.album?.imageUrl).into(sivAlbumImage)

            tvAlbumName.apply {
                text = model.name
                isSelected = true
            }

            tvArtistName.apply {
                text = model.artistsNames
                isSelected = true
            }
        }

        fun setColorItemView() {
            itemView.foreground = AppCompatResources.getDrawable(itemView.context, R.drawable.foreground_rounded_action_down)
        }

        fun removeColorItemView() {
            itemView.foreground = null
        }
    }
}