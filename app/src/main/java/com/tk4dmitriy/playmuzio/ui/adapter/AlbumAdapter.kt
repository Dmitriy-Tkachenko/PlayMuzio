package com.tk4dmitriy.playmuzio.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Item
import java.util.concurrent.TimeUnit

class AlbumAdapter: RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    interface Callback {
        fun touchOnView(item: Item, view: View, action: Int)
    }
    private val tracks: MutableList<Item> = mutableListOf()
    private lateinit var callback: Callback

    fun attachCallback(callback: Callback) {
        this.callback = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(tracks: List<Item>) {
        this.tracks.addAll(tracks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_track_item, parent,false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: AlbumAdapter.ViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(model = track)
    }

    override fun getItemCount() = tracks.size

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val songNumber: TextView = view.findViewById(R.id.song_number)
        private val songName: TextView = view.findViewById(R.id.song_name)
        private val songArtists: TextView = view.findViewById(R.id.song_artis)
        private val songTime: TextView = view.findViewById(R.id.song_time)
        private val btnMore: ImageView = view.findViewById(R.id.btn_more)

        init {
            view.setOnClickListener {  }
            view.setOnTouchListener { v, event ->
                callback.touchOnView(item = tracks[adapterPosition], view = view, action = event.action)
                v?.onTouchEvent(event) ?: true
            }
            btnMore.setOnClickListener {
                Toast.makeText(view.context, "More", Toast.LENGTH_SHORT).show()
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: Item) {
            songNumber.apply {
                text = "${adapterPosition + 1}"
                isSelected = true
            }
            songName.apply {
                text = model.name
                isSelected = true
            }
            songArtists.apply {
                text = model.artistsNames
                isSelected = true
            }
            songTime.text = model.durationMin
        }
    }
}