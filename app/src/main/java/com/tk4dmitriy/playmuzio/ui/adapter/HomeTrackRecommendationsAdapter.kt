package com.tk4dmitriy.playmuzio.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.Artist
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.Track

class HomeTrackRecommendationsAdapter: RecyclerView.Adapter<HomeTrackRecommendationsAdapter.ViewHolder>() {
    interface Callback {
        fun touchOnView(track: Track, view: View, action: Int)
    }

    private val trackRecommendations: MutableList<Track> = mutableListOf()
    private lateinit var callback: Callback

    fun attachCallback(callback: Callback) {
        this.callback = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(recommendedTracks: List<Track>) {
        this.trackRecommendations.addAll(recommendedTracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_recommendation_item, parent,false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommendedTracks = trackRecommendations[position]
        holder.bind(recommendedTracks)
    }

    override fun getItemCount() = trackRecommendations.size


    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val sivAlbumImage: ShapeableImageView = view.findViewById(R.id.album_image)
        private val tvAlbumName: TextView = view.findViewById(R.id.album_name)
        private val tvArtistName: TextView = view.findViewById(R.id.artist_name)

        init {
            view.setOnClickListener {  }
            view.setOnTouchListener { v, event ->
                callback.touchOnView(trackRecommendations[adapterPosition], view, event.action)
                v?.onTouchEvent(event) ?: true
            }
        }

        fun bind(model: Track) {
            if (model.album?.images != null) {
                for (image in model.album.images) {
                    if (image.height == 300) {
                        Picasso.get().load(image.url).into(sivAlbumImage)
                    }
                }
            }
            tvAlbumName.apply {
                text = model.name
                isSelected = true
            }

            tvArtistName.apply {
                text = getArtists(model.artists)
                isSelected = true
            }
        }

        private fun getArtists(artists: List<Artist>?): String {
            var result = ""

            if (artists != null) {
                for (index in artists.indices) {
                    if (artists.size > 1 && index != artists.size - 1) {
                        result += "${artists[index].name}, "
                    }
                }
                result += artists[artists.size - 1].name
            }

            return result
        }
    }
}