package com.tk4dmitriy.playmuzio.ui.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Album
import com.tk4dmitriy.playmuzio.data.model.endpoints.album.Item
import com.tk4dmitriy.playmuzio.ui.adapter.*
import com.tk4dmitriy.playmuzio.ui.viewmodel.AlbumViewModel
import com.tk4dmitriy.playmuzio.ui.viewmodel.ViewModelFactory
import com.tk4dmitriy.playmuzio.utils.Status
import com.tk4dmitriy.playmuzio.utils.TAG
import jp.wasabeef.picasso.transformations.BlurTransformation

class AlbumActivity: AppCompatActivity() {
    private lateinit var ivAlbumBlur: ImageView
    private lateinit var sivAlbum: ShapeableImageView
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolBarLayout: Toolbar
    private lateinit var tvArtistName: TextView
    private lateinit var tvInfo : TextView
    private lateinit var rvAlbumTracks: RecyclerView
    private lateinit var albumTracksAdapter: AlbumAdapter
    private var url: String? = null

    private val albumViewModel: AlbumViewModel by lazy {
        ViewModelProvider(owner = this,
            ViewModelFactory(apiHelper = ApiHelper(RetrofitBuilder.apiService))
        )[AlbumViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        setupUI()
        callbackProcessing()

        intent.extras?.run {
            url = this.getString("URL")
            url?.run { observeAlbum(url = this) }
        }
    }

    private fun observeAlbum(url: String) {
        albumViewModel.fetchAlbum(url = url).observe(this) { resultAlbum ->
            val success = fun() {
                resultAlbum.data?.body()?.run {
                    bind(this)
                    tracks.items.run {
                        albumTracksAdapter.setData(this)
                    }
                }
            }
            val loading = fun() {

            }
            val error = fun() {
                Toast.makeText(this, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultAlbum.message.toString())
            }
            observeDataProcessing(resultAlbum.status, success = success, loading = loading, error = error)
        }
    }

    private fun observeDataProcessing(status: Status, success: () -> Unit, loading: () -> Unit, error: () -> Unit) {
        when (status) {
            Status.SUCCESS -> success()
            Status.LOADING -> loading()
            Status.ERROR -> error()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_album, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        albumTracksAdapter = AlbumAdapter()

        ivAlbumBlur = findViewById(R.id.iv_album_blur)
        sivAlbum = findViewById(R.id.siv_album)
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        appBarLayout = findViewById(R.id.appBarLayout)
        toolBarLayout = findViewById(R.id.toolbar)
        tvArtistName = findViewById(R.id.tv_artist_name)
        tvInfo = findViewById(R.id.tv_info)
        rvAlbumTracks = findViewById(R.id.rv_album_tracks)

        rvAlbumTracks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = albumTracksAdapter
        }

        setSupportActionBar(toolBarLayout)
        deleteExtraPadding()
    }

    private fun callbackProcessing() {
        albumTracksAdapter.attachCallback(object: AlbumAdapter.Callback {
            override fun touchOnView(item: Item, view: View, action: Int) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> backgroundViewActionDown(view = view)
                    MotionEvent.ACTION_UP -> animateViewActionUp(view = view)
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel(view = view)
                }
            }
        })
    }

    private fun deleteExtraPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            (toolBarLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun bind(album: Album) {
        album.images.forEach { image ->
            if (image.width == 640) {
                Picasso.get()
                    .load(image.url)
                    .transform(BlurTransformation(this, 25, 2))
                    .into(ivAlbumBlur)
            }
            if (image.width == 300) {
                Picasso.get()
                    .load(image.url)
                    .into(sivAlbum)
            }
        }
        collapsingToolbarLayout.title = album.name

        tvArtistName.apply {
            text = album.artistNames
            isSelected = true
        }

        tvInfo.apply {
            text = album.tracks.info
        }
    }

    private fun backgroundViewActionDown(view: View) {
        view.background = AppCompatResources.getDrawable(view.context, R.drawable.foreground_action_down)
    }

    private fun animateViewActionUp(view: View) {
        val animator: ObjectAnimator = ObjectAnimator.ofInt(view.background, "alpha", 255, 0)
        animator.duration = 300
        animator.start()
    }

    private fun animateViewActionCancel(view: View) {
        val animator: ObjectAnimator = ObjectAnimator.ofInt(view.background, "alpha", 255, 0)
        animator.duration = 300
        animator.start()
    }
}