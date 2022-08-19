package com.tk4dmitriy.playmuzio.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.Item

class HomeFeaturedPlaylistsAdapter: RecyclerView.Adapter<HomeFeaturedPlaylistsAdapter.ViewHolder>() {
    private val featuredPlaylists: MutableList<Item> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(featuredPlaylists: List<Item>) {
        this.featuredPlaylists.addAll(featuredPlaylists)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.featured_playlist_item, parent,false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val featuredPlaylist = featuredPlaylists[position]
        holder.bind(featuredPlaylist)
    }

    override fun getItemCount() = featuredPlaylists.size


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val playlistsImage: ShapeableImageView = view.findViewById(R.id.siv_playlist)

        fun bind(model: Item) {
            for (image in model.images) {
                if (image.height == 300 || image.height == null) {
                    Picasso.get().load(image.url).into(playlistsImage)
                }
            }
        }
    }
}