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
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.Artist
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.Item

class NewReleasesAdapter: RecyclerView.Adapter<NewReleasesAdapter.ViewHolder>() {
    interface Callback {
        fun touchOnView(item: Item, view: View, action: Int)
    }
    private val newReleases: MutableList<Item> = mutableListOf()
    private lateinit var callback: Callback

    fun attachCallback(callback: Callback) {
        this.callback = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newReleases: List<Item>) {
        this.newReleases.addAll(newReleases)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_release_item, parent,false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newRelease = newReleases[position]
        holder.bind(newRelease)
    }

    override fun getItemCount() = newReleases.size


    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val sivAlbumImage: ShapeableImageView = view.findViewById(R.id.siv_album_image)
        private val tvAlbumName: TextView = view.findViewById(R.id.tv_album_name)
        private val tvArtistName: TextView = view.findViewById(R.id.tv_artist_name)

        init {
            view.setOnClickListener {  }
            view.setOnTouchListener { v, event ->
                callback.touchOnView(item = newReleases[adapterPosition], view = view, action = event.action)
                v?.onTouchEvent(event) ?: true
            }
        }

        fun bind(model: Item) {
            for (image in model.images) {
                if (image.height == 300 || image.height == 0) Picasso.get().load(image.url).into(sivAlbumImage)
            }

            tvAlbumName.apply {
                text = model.name
                isSelected = true
            }

            tvArtistName.apply {
                text = model.artistsNames
                isSelected = true
            }
        }
    }
}