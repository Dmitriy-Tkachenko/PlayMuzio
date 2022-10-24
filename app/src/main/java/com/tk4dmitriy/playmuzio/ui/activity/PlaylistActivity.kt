package com.tk4dmitriy.playmuzio.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.data.service.ExoPlayerService
import com.tk4dmitriy.playmuzio.ui.adapter.PlaylistAdapter
import com.tk4dmitriy.playmuzio.ui.viewmodel.PlaylistViewModel
import com.tk4dmitriy.playmuzio.ui.viewmodel.ViewModelFactory
import com.tk4dmitriy.playmuzio.utils.Status
import com.tk4dmitriy.playmuzio.utils.TAG
import jp.wasabeef.glide.transformations.BlurTransformation


class PlaylistActivity: AppCompatActivity() {
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolBarLayout: Toolbar
    private lateinit var ivPlaylistBlur: ImageView
    private lateinit var sivPlaylist: ShapeableImageView
    private lateinit var tvDescription: TextView
    private lateinit var tvInfo: TextView
    private lateinit var rvPlaylistTracks: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var playlistAdapter: PlaylistAdapter
    private var trackPreviewUrls: ArrayList<String> = arrayListOf()
    private var trackNames: ArrayList<String> = arrayListOf()
    private var trackArtistNames: ArrayList<String> = arrayListOf()
    private var trackImageUrls: ArrayList<String> = arrayListOf()

    private val playlistViewModel: PlaylistViewModel by lazy {
        ViewModelProvider(owner = this,
            ViewModelFactory(apiHelper = ApiHelper(RetrofitBuilder.get().apiService))
        )[PlaylistViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        setupUI()
        callbackProcessing()

        intent.extras?.run {
            val playlistUrl = this.getString(PLAYLIST_URL)
            val playlistImageUrl = this.getString(PLAYLIST_IMAGE_URL)
            val playlistName = this.getString(PLAYLIST_NAME)
            val playlistDescription = this.getString(PLAYLIST_DESCRIPTION)
            bindData(playlistImageUrl = playlistImageUrl!!, playlistName = playlistName!!, playlistDescription = playlistDescription!!)
            observePlaylist(url = playlistUrl!!)
        }
    }

    override fun onResume() {
        super.onResume()
        if (trackPreviewUrls.contains(ExoPlayerService.CURRENT_TRACK_URL) && ExoPlayerService.CURRENT_TRACK_URL.isNotEmpty()) {
            val positionTrackPlayed = trackPreviewUrls.indexOf(ExoPlayerService.CURRENT_TRACK_URL)
            playlistAdapter.deselectOldTrack()
            playlistAdapter.selectNewTrack(positionTrackPlayed)
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
        playlistAdapter = PlaylistAdapter()

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar)
        appBarLayout = findViewById(R.id.appBarLayout)
        toolBarLayout = findViewById(R.id.toolbar)
        ivPlaylistBlur = findViewById(R.id.iv_playlist_blur)
        sivPlaylist = findViewById(R.id.siv_playlist)
        tvDescription = findViewById(R.id.tv_description)
        tvInfo = findViewById(R.id.tv_info)
        rvPlaylistTracks = findViewById(R.id.rv_playlist_tracks)
        progressBar = findViewById(R.id.progress_bar)

        rvPlaylistTracks.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = playlistAdapter
        }

        val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0

        setSupportActionBar(toolBarLayout)
        deleteExtraPadding()
    }

    private fun bindData(playlistImageUrl: String, playlistName: String, playlistDescription: String) {
        Glide.with(this@PlaylistActivity)
            .load(playlistImageUrl)
            .transform(BlurTransformation(50, 4))
            .into(ivPlaylistBlur)
        Glide.with(this@PlaylistActivity)
            .load(playlistImageUrl)
            .into(sivPlaylist)

        collapsingToolbarLayout.title = playlistName

        tvDescription.apply {
            text = playlistDescription
            isSelected = true
        }
    }

    private fun callbackProcessing() {
        playlistAdapter.attachCallback(object: PlaylistAdapter.Callback {
            override fun touchUp(position: Int) {
                if (trackPreviewUrls[position].isNotEmpty()) {
                    startTrackPlayActivity(position)
                    playlistAdapter.deselectOldTrack()
                    playlistAdapter.selectNewTrack(position)
                } else {
                    playlistAdapter.deselectTrack(position)
                    Toast.makeText(this@PlaylistActivity, "Отсутсвует превью трека", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun startTrackPlayActivity(position: Int) {
        val intent = Intent(this@PlaylistActivity, TrackPlayActivity::class.java)
        intent.action = TrackPlayActivity.START_FROM_PLAYLIST
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

    private fun observePlaylist(url: String) {
        playlistViewModel.fetchPlaylist(url = url).observe(this) { resultPlaylist ->
            val success = fun() {
                resultPlaylist.data?.body()?.tracks?.let { tracks ->
                    tracks.items?.run { playlistAdapter.setData(this) }
                    tvInfo.apply {
                        text = tracks.info
                    }

                    trackPreviewUrls.addAll(tracks.trackPreviewUrls)
                    trackNames.addAll(tracks.trackNames)
                    trackArtistNames.addAll(tracks.trackArtistNames)
                    trackImageUrls.addAll(tracks.trackImageUrls)

                    if (tracks.trackPreviewUrls.isNotEmpty()) {
                        if (tracks.trackPreviewUrls.contains(ExoPlayerService.CURRENT_TRACK_URL) && ExoPlayerService.CURRENT_TRACK_URL.isNotEmpty()) {
                            val positionTrackPlayed = tracks.trackPreviewUrls.indexOf(ExoPlayerService.CURRENT_TRACK_URL)
                            playlistAdapter.selectNewTrack(positionTrackPlayed)
                        }
                    }

                    val params = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    progressBar.visibility = View.GONE
                    rvPlaylistTracks.visibility = View.VISIBLE
                }
            }
            val loading = fun() {
                progressBar.visibility = View.VISIBLE
                rvPlaylistTracks.visibility = View.GONE
            }
            val error = fun() {
                Toast.makeText(this, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultPlaylist?.message.toString())
            }
            observeDataProcessing(resultPlaylist.status, success = success, loading = loading, error = error)
        }
    }

    private fun observeDataProcessing(status: Status, success: () -> Unit, loading: () -> Unit, error: () -> Unit) {
        when (status) {
            Status.SUCCESS -> success()
            Status.LOADING -> loading()
            Status.ERROR -> error()
        }
    }

    companion object {
        const val PLAYLIST_URL = "PLAYLIST_URL"
        const val PLAYLIST_NAME = "PLAYLIST_NAME"
        const val PLAYLIST_DESCRIPTION = "PLAYLIST_DESCRIPTION"
        const val PLAYLIST_IMAGE_URL = "PLAYLIST_IMAGE_URL"
    }
}