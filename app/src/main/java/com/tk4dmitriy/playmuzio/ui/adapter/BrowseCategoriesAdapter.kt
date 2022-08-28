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
import com.tk4dmitriy.playmuzio.data.model.endpoints.browseCategories.Item

class BrowseCategoriesAdapter: RecyclerView.Adapter<BrowseCategoriesAdapter.ViewHolder>() {
    interface Callback {
        fun touchOnView(item: Item, view: View, action: Int)
    }
    private val browseCategories: MutableList<Item> = mutableListOf()
    private lateinit var callback: Callback

    fun attachCallback(callback: Callback) {
        this.callback = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newReleases: List<Item>) {
        this.browseCategories.addAll(newReleases)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.browse_category_item, parent,false)
        return ViewHolder(view = view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newRelease = browseCategories[position]
        holder.bind(newRelease)
    }

    override fun getItemCount() = browseCategories.size


    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val sivAlbumImage: ShapeableImageView = view.findViewById(R.id.siv_category_image)
        private val tvCategoryName: TextView = view.findViewById(R.id.tv_category_name)

        init {
            view.setOnClickListener {  }
            view.setOnTouchListener { v, event ->
                callback.touchOnView(item = browseCategories[adapterPosition], view = view, action = event.action)
                v?.onTouchEvent(event) ?: true
            }
        }

        fun bind(model: Item) {
            for (image in model.icons) {
                Picasso.get().load(image.url).into(sivAlbumImage)
            }

            tvCategoryName.apply {
                text = model.name
                isSelected = true
            }
        }
    }
}