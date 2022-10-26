package com.tk4dmitriy.playmuzio.ui.activity

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.data.service.ExoPlayerService
import com.tk4dmitriy.playmuzio.ui.adapter.*
import com.tk4dmitriy.playmuzio.ui.viewmodel.AlbumViewModel
import com.tk4dmitriy.playmuzio.ui.viewmodel.ViewModelFactory
import com.tk4dmitriy.playmuzio.utils.Status
import com.tk4dmitriy.playmuzio.utils.TAG
import jp.wasabeef.glide.transformations.BlurTransformation


class AlbumActivity: AppCompatActivity() {
    private lateinit var ivAlbumBlur: ImageView
    private lateinit var sivAlbum: ShapeableImageView
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolBarLayout: Toolbar
    private lateinit var tvArtistName: TextView
    private lateinit var tvInfo : TextView
    private lateinit var rvAlbumTracks: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var albumTracksAdapter: AlbumAdapter
    private var trackPreviewUrls: ArrayList<String> = arrayListOf()
    private var trackNames: ArrayList<String> = arrayListOf()
    private var trackArtistNames: ArrayList<String> = arrayListOf()
    private var trackImageUrls: ArrayList<String> = arrayListOf()
    private var albumImageUrl: String = ""

    private val albumViewModel: AlbumViewModel by lazy {
        ViewModelProvider(owner = this,
            ViewModelFactory(apiHelper = ApiHelper(RetrofitBuilder.get().apiService))
        )[AlbumViewModel::class.java]
    }

    private var isReceiver = false
    private var intentFilter: IntentFilter? = null
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ExoPlayerService.UPDATE_UI) {
                if (ExoPlayerService.CURRENT_TRACK_URL.isNotEmpty()) {
                    albumTracksAdapter.deselectOldTrack()
                    if (trackPreviewUrls.contains(ExoPlayerService.CURRENT_TRACK_URL)) {
                        val positionTrackPlayed = trackPreviewUrls.indexOf(ExoPlayerService.CURRENT_TRACK_URL)
                        albumTracksAdapter.selectNewTrack(positionTrackPlayed)
                    }
                }
            }
            isReceiver = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        setupUI()
        callbackProcessing()

        intent.extras?.run {
            val albumUrl = this.getString(ALBUM_URL) ?: ""
            val albumName = this.getString(ALBUM_NAME) ?: ""
            val albumArtistsName = this.getString(ALBUM_ARTISTS_NAME) ?: ""
            albumImageUrl = this.getString(ALBUM_IMAGE_URL) ?: ""
            bindData(albumImageUrl, albumName, albumArtistsName)
            observeAlbum(url = albumUrl)
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

    override fun onStart() {
        super.onStart()
        intentFilter = IntentFilter()
        intentFilter!!.addAction(ExoPlayerService.UPDATE_UI)
        registerReceiver(receiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        if (isReceiver) {
            unregisterReceiver(receiver)
            isReceiver = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (trackPreviewUrls.contains(ExoPlayerService.CURRENT_TRACK_URL) && ExoPlayerService.CURRENT_TRACK_URL.isNotEmpty()) {
            val positionTrackPlayed = trackPreviewUrls.indexOf(ExoPlayerService.CURRENT_TRACK_URL)
            albumTracksAdapter.deselectOldTrack()
            albumTracksAdapter.selectNewTrack(positionTrackPlayed)
        }
    }

    private fun observeAlbum(url: String) {
        albumViewModel.fetchAlbum(url = url).observe(this) { resultAlbum ->
            val success = fun() {
                resultAlbum.data?.body()?.tracks?.let { tracks ->
                    tracks.items?.run {
                        albumTracksAdapter.setData(this)
                    }

                    tvInfo.apply {
                        text = tracks.info
                    }

                    trackPreviewUrls.addAll(tracks.trackPreviewUrls)
                    trackNames.addAll(tracks.trackNames)
                    trackArtistNames.addAll(tracks.trackArtistNames)
                    trackImageUrls.add(albumImageUrl)

                    if (tracks.trackPreviewUrls.isNotEmpty()) {
                        if (tracks.trackPreviewUrls.contains(ExoPlayerService.CURRENT_TRACK_URL) && ExoPlayerService.CURRENT_TRACK_URL.isNotEmpty()) {
                            val positionTrackPlayed = tracks.trackPreviewUrls.indexOf(ExoPlayerService.CURRENT_TRACK_URL)
                            albumTracksAdapter.selectNewTrack(positionTrackPlayed)
                        }
                    }

                    val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    progressBar.visibility = View.GONE
                    rvAlbumTracks.visibility = View.VISIBLE
                }
            }
            val loading = fun() {
                progressBar.visibility = View.VISIBLE
                rvAlbumTracks.visibility = View.GONE
            }
            val error = fun() {
                Toast.makeText(this, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultAlbum?.message.toString())
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
        progressBar = findViewById(R.id.progress_bar)

        rvAlbumTracks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = albumTracksAdapter
        }

        (rvAlbumTracks.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0

        setSupportActionBar(toolBarLayout)
        deleteExtraPadding()
    }

    private fun callbackProcessing() {
        albumTracksAdapter.attachCallback(object: AlbumAdapter.Callback {
            override fun touchUp(position: Int) {
                if (trackPreviewUrls[position].isNotEmpty()) {
                    startTrackPlayActivity(position)
                    albumTracksAdapter.deselectOldTrack()
                    albumTracksAdapter.selectNewTrack(position)
                } else {
                    albumTracksAdapter.deselectTrack(position)
                    Toast.makeText(this@AlbumActivity, "Отсутсвует превью трека", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun startTrackPlayActivity(position: Int) {
        val intent = Intent(this@AlbumActivity, TrackPlayActivity::class.java)
        intent.action = TrackPlayActivity.START_FROM_ALBUM
        intent.putStringArrayListExtra(TrackPlayActivity.TRACK_PREVIEW_URLS, trackPreviewUrls)
        intent.putStringArrayListExtra(TrackPlayActivity.TRACK_NAMES, trackNames)
        intent.putStringArrayListExtra(TrackPlayActivity.TRACK_ARTISTS, trackArtistNames)
        intent.putStringArrayListExtra(TrackPlayActivity.TRACK_IMAGE_URLS, trackImageUrls)
        intent.putExtra(TrackPlayActivity.TRACK_POSITION, position)
        startActivity(intent)
    }

    private fun deleteExtraPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            (toolBarLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun bindData(albumImageUrl: String, albumName: String, albumArtistsName: String) {
        Glide.with(this@AlbumActivity)
            .load(albumImageUrl)
            .transform(BlurTransformation(50, 4))
            .into(ivAlbumBlur)
        Glide.with(this@AlbumActivity)
            .load(albumImageUrl)
            .into(sivAlbum)

        collapsingToolbarLayout.title = albumName

        tvArtistName.apply {
            text = albumArtistsName
            isSelected = true
        }
    }

    companion object {
        const val ALBUM_URL = "ALBUM_URL"
        const val ALBUM_IMAGE_URL = "ALBUM_IMAGE_URL"
        const val ALBUM_NAME = "ALBUM_NAME"
        const val ALBUM_ARTISTS_NAME = "ALBUM_ARTISTS_NAME"
    }
}