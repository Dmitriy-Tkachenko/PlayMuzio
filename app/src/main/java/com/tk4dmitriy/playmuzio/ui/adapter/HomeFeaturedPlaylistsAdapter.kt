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
    interface Callback {
        fun touchOnView(item: Item, view: View, action: Int)
    }

    private val featuredPlaylists: MutableList<Item> = mutableListOf()
    private lateinit var callback: Callback

    fun attachCallback(callback: Callback) {
        this.callback = callback
    }

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

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val playlistsImage: ShapeableImageView = view.findViewById(R.id.siv_playlist)

        init {
            view.setOnClickListener {  }
            view.setOnTouchListener { v, event ->
                callback.touchOnView(featuredPlaylists[adapterPosition], view, event.action)
                v?.onTouchEvent(event) ?: true
            }
        }

        fun bind(model: Item) {
            if (model.images != null) {
                for (image in model.images) {
                    if ((image.height == 300 || image.height == null) && image.url != null && image.url.isNotEmpty()) {
                        Picasso.get().load(image.url).into(playlistsImage)
                    }
                }
            }
        }
    }
}