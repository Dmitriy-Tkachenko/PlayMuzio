package com.tk4dmitriy.playmuzio.ui.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.Item

class HomeFeaturedPlaylistsAdapter: RecyclerView.Adapter<HomeFeaturedPlaylistsAdapter.ViewHolder>() {
    interface Callback {
        fun touchUp(item: Item)
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
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> foregroundViewActionDown()
                    MotionEvent.ACTION_UP -> {
                        animateViewActionUp()
                        callback.touchUp(featuredPlaylists[bindingAdapterPosition])
                    }
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel()
                }
                v?.onTouchEvent(event) ?: true
            }
        }

        fun bind(model: Item) {
            Glide.with(itemView).load(model.imageUrl).into(playlistsImage)
        }

        private fun foregroundViewActionDown() {
            itemView.foreground = AppCompatResources.getDrawable(itemView.context, R.drawable.foreground_rounded_action_down)
        }

        private fun animateViewActionUp() {
            val animator: ObjectAnimator = ObjectAnimator.ofInt(itemView.foreground, "alpha", 255, 0)
            animator.duration = 300
            animator.start()
        }

        private fun animateViewActionCancel() {
            val animator: ObjectAnimator = ObjectAnimator.ofInt(itemView.foreground, "alpha", 255, 0)
            animator.duration = 300
            animator.start()
        }
    }
}