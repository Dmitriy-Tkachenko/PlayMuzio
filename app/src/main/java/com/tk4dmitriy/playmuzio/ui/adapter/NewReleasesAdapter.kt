package com.tk4dmitriy.playmuzio.ui.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.Artists
import com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.Item

class NewReleasesAdapter: RecyclerView.Adapter<NewReleasesAdapter.ViewHolder>() {
    private val newReleases: MutableList<Item> = mutableListOf()
    var onItemClick: ((Item) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newReleases: List<Item>) {
        this.newReleases.addAll(newReleases)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_releases_item, parent,false)
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
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        view.foreground = AppCompatResources.getDrawable(view.context, R.drawable.foreground_action_down)
                    }
                    MotionEvent.ACTION_UP -> {
                        val animator: ObjectAnimator = ObjectAnimator.ofInt(view.foreground, "alpha", 255, 0)
                        animator.duration = 300
                        animator.start()
                        onItemClick?.invoke(newReleases[adapterPosition])
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        val animator: ObjectAnimator = ObjectAnimator.ofInt(view.foreground, "alpha", 255, 0)
                        animator.duration = 300
                        animator.start()
                    }
                }

                v?.onTouchEvent(event) ?: true
            }
        }

        fun bind(model: Item) {
            if (model.images != null) {
                for (image in model.images) {
                    if ((image.height == 300 || image.height == null) && image.url != null && image.url.isNotEmpty()) {
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

        private fun getArtists(artists: List<Artists>?): String {
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