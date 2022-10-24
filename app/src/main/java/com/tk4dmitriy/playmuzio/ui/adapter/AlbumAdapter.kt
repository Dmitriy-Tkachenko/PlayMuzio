package com.tk4dmitriy.playmuzio.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Item

private const val SET_COLOR_ITEM_VIEW = "SET_COLOR_ITEM_VIEW"
private const val REMOVE_COLOR_ITEM_VIEW = "REMOVE_COLOR_ITEM_VIEW"

class AlbumAdapter: RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    interface Callback {
        fun touchUp(position: Int)
    }

    private val tracks: MutableList<Item> = mutableListOf()
    private var selectedTrack = -1
    private lateinit var callback: Callback

    fun attachCallback(callback: Callback) {
        this.callback = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(tracks: List<Item>) {
        this.tracks.addAll(tracks)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_track_item, parent,false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: AlbumAdapter.ViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(model = track)
        if (position == selectedTrack) {
            holder.setColorItemView()
        } else {
            holder.removeColorItemView()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads)
        else {
            for (payload in payloads) {
                if (payload == SET_COLOR_ITEM_VIEW) holder.setColorItemView()
                else if (payload == REMOVE_COLOR_ITEM_VIEW) holder.removeColorItemView()
            }
        }
    }

    override fun getItemCount() = tracks.size

    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val songNumber: TextView = view.findViewById(R.id.song_number)
        private val songName: TextView = view.findViewById(R.id.song_name)
        private val songArtists: TextView = view.findViewById(R.id.song_artis)
        private val songTime: TextView = view.findViewById(R.id.song_time)
        private val btnAdd: ImageView = view.findViewById(R.id.btn_add)

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
            btnAdd.setOnClickListener {
                Toast.makeText(view.context, "More", Toast.LENGTH_SHORT).show()
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: Item) {
            songNumber.apply {
                text = "${bindingAdapterPosition + 1}"
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

        fun setColorItemView() {
            itemView.background = AppCompatResources.getDrawable(itemView.context, R.drawable.foreground_action_down)
        }

        fun removeColorItemView() {
            itemView.background = null
        }
    }
}