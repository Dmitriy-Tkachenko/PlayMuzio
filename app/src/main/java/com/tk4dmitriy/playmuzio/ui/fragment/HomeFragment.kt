package com.tk4dmitriy.playmuzio.ui.fragment

import android.animation.ObjectAnimator
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.Item
import com.tk4dmitriy.playmuzio.data.service.ExoPlayerService
import com.tk4dmitriy.playmuzio.ui.activity.AlbumActivity
import com.tk4dmitriy.playmuzio.ui.activity.PlaylistActivity
import com.tk4dmitriy.playmuzio.ui.activity.TrackPlayActivity
import com.tk4dmitriy.playmuzio.ui.adapter.HomeFeaturedPlaylistsAdapter
import com.tk4dmitriy.playmuzio.ui.adapter.HomeTrackRecommendationsAdapter
import com.tk4dmitriy.playmuzio.ui.adapter.NewReleasesAdapter
import com.tk4dmitriy.playmuzio.ui.viewmodel.HomeViewModel
import com.tk4dmitriy.playmuzio.ui.viewmodel.ViewModelFactory
import com.tk4dmitriy.playmuzio.utils.Status
import com.tk4dmitriy.playmuzio.utils.TAG

class HomeFragment: Fragment() {
    private lateinit var llFeaturedPlaylists: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var tvFeaturedPlaylists: TextView
    private lateinit var tvNewReleases: TextView
    private lateinit var tvTrackRecommendations: TextView
    private lateinit var rvFeaturedPlaylists: RecyclerView
    private lateinit var rvNewReleases: RecyclerView
    private lateinit var rvTrackRecommendations: RecyclerView
    private lateinit var featuredPlaylistsAdapter: HomeFeaturedPlaylistsAdapter
    private lateinit var newReleasesAdapter: NewReleasesAdapter
    private lateinit var trackRecommendationsAdapter: HomeTrackRecommendationsAdapter

    private var trackPreviewUrls: ArrayList<String> = arrayListOf()
    private var trackNames: ArrayList<String> = arrayListOf()
    private var trackArtistNames: ArrayList<String> = arrayListOf()
    private var trackImageUrls: ArrayList<String> = arrayListOf()

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(owner = this,
            ViewModelFactory(apiHelper = ApiHelper(RetrofitBuilder.get().apiService))
        )[HomeViewModel::class.java]
    }

    private var isReceiver = false
    private var intentFilter: IntentFilter? = null
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ExoPlayerService.UPDATE_UI) {
                if (ExoPlayerService.CURRENT_TRACK_URL.isNotEmpty()) {
                    trackRecommendationsAdapter.deselectOldTrack()
                    if (trackPreviewUrls.contains(ExoPlayerService.CURRENT_TRACK_URL)) {
                        val positionTrackPlayed = trackPreviewUrls.indexOf(ExoPlayerService.CURRENT_TRACK_URL)
                        trackRecommendationsAdapter.selectNewTrack(positionTrackPlayed)
                    }
                }
            }
            isReceiver = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        featuredPlaylistsAdapter = HomeFeaturedPlaylistsAdapter()
        newReleasesAdapter = NewReleasesAdapter()
        trackRecommendationsAdapter = HomeTrackRecommendationsAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupUI(view = view)
        callbackProcessing()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFeaturedPlaylists()
        observeNewReleases()
        observeTrackRecommendations()
    }

    override fun onStart() {
        super.onStart()
        intentFilter = IntentFilter()
        intentFilter!!.addAction(ExoPlayerService.UPDATE_UI)
        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        if (isReceiver) {
            requireActivity().unregisterReceiver(receiver)
            isReceiver = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (trackPreviewUrls.contains(ExoPlayerService.CURRENT_TRACK_URL) && ExoPlayerService.CURRENT_TRACK_URL.isNotEmpty()) {
            val positionTrackPlayed = trackPreviewUrls.indexOf(ExoPlayerService.CURRENT_TRACK_URL)
            trackRecommendationsAdapter.deselectOldTrack()
            trackRecommendationsAdapter.selectNewTrack(positionTrackPlayed)
        } else {
            trackRecommendationsAdapter.deselectOldTrack()
        }
    }

    private fun setupUI(view: View) {
        llFeaturedPlaylists = view.findViewById(R.id.ll_featured_playlists)
        etSearch = view.findViewById(R.id.et_search)

        tvNewReleases = view.findViewById(R.id.tv_new_releases)
        tvFeaturedPlaylists = view.findViewById(R.id.tv_featured_playlists)
        tvTrackRecommendations = view.findViewById(R.id.tv_may_like)

        rvFeaturedPlaylists = view.findViewById(R.id.rv_featured_playlists)
        rvNewReleases = view.findViewById(R.id.rv_new_releases)
        rvTrackRecommendations = view.findViewById(R.id.rv_track_recommendations)

        rvFeaturedPlaylists.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = featuredPlaylistsAdapter
        }

        rvNewReleases.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newReleasesAdapter
        }

        rvTrackRecommendations.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = trackRecommendationsAdapter
        }
    }

    private fun callbackProcessing() {
        featuredPlaylistsAdapter.attachCallback(object: HomeFeaturedPlaylistsAdapter.Callback {
            override fun touchUp(item: Item) {
                val intent = Intent(requireActivity(), PlaylistActivity::class.java)
                intent.putExtra(PlaylistActivity.PLAYLIST_URL, item.href)
                intent.putExtra(PlaylistActivity.PLAYLIST_NAME, item.name)
                intent.putExtra(PlaylistActivity.PLAYLIST_DESCRIPTION, item.description)
                intent.putExtra(PlaylistActivity.PLAYLIST_IMAGE_URL, item.imageUrl)
                startActivity(intent)
            }
        })
        newReleasesAdapter.attachCallback(object: NewReleasesAdapter.Callback {
            override fun touchOnView(item: com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.Item, view: View, action: Int) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> foregroundViewActionDown(view = view)
                    MotionEvent.ACTION_UP -> {
                        animateViewActionUp(view = view)
                        val intent = Intent(requireActivity(), AlbumActivity::class.java)
                        intent.putExtra(AlbumActivity.ALBUM_URL, item.href)
                        intent.putExtra(AlbumActivity.ALBUM_IMAGE_URL, item.imageUrl)
                        intent.putExtra(AlbumActivity.ALBUM_NAME, item.name)
                        intent.putExtra(AlbumActivity.ALBUM_ARTISTS_NAME, item.artistsNames)
                        startActivity(intent)
                    }
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel(view = view)
                }
            }
        })
        trackRecommendationsAdapter.attachCallback(object: HomeTrackRecommendationsAdapter.Callback {
            override fun touchUp(position: Int) {
                if (trackPreviewUrls[position].isNotEmpty()) {
                    val intent = Intent(requireActivity(), TrackPlayActivity::class.java)
                    intent.action = TrackPlayActivity.START_FROM_RECOMMENDATIONS
                    intent.putStringArrayListExtra(TrackPlayActivity.TRACK_PREVIEW_URLS, trackPreviewUrls)
                    intent.putStringArrayListExtra(TrackPlayActivity.TRACK_NAMES, trackNames)
                    intent.putStringArrayListExtra(TrackPlayActivity.TRACK_ARTISTS, trackArtistNames)
                    intent.putStringArrayListExtra(TrackPlayActivity.TRACK_IMAGE_URLS, trackImageUrls)
                    intent.putExtra(TrackPlayActivity.TRACK_POSITION, position)
                    startActivity(intent)
                    trackRecommendationsAdapter.deselectOldTrack()
                    trackRecommendationsAdapter.selectNewTrack(position)
                } else {
                    trackRecommendationsAdapter.deselectTrack(position)
                    Toast.makeText(requireActivity(), "Отсутсвует превью трека", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun observeFeaturedPlaylists() {
        homeViewModel.fetchFeaturedPlaylists().observe(viewLifecycleOwner) { resultPlaylists ->
            val success = fun() {
                resultPlaylists.data?.body()?.let { featuredPlaylists ->
                    Log.d(this@HomeFragment.TAG, featuredPlaylists.toString())
                    featuredPlaylists.playlists?.run {
                        tvFeaturedPlaylists.apply {
                            text = featuredPlaylists.message
                            isSelected = true
                        }
                        items?.run {
                            featuredPlaylistsAdapter.setData(this)
                            visibleFeaturedPlaylists()
                        }
                    }
                }
            }
            val loading = fun() {
            }
            val error = fun() {
                Toast.makeText(requireActivity(), resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(this@HomeFragment.TAG, resultPlaylists.message.toString())
            }
            observeDataProcessing(status = resultPlaylists.status, success = success, loading = loading, error = error)
        }
    }

    private fun observeNewReleases() {
        homeViewModel.fetchNewReleases().observe(viewLifecycleOwner) { resultReleases ->
            val success = fun() {
                resultReleases.data?.body()?.albums?.items?.run {
                    newReleasesAdapter.setData(this)
                    visibleNewReleases()
                }
            }
            val loading = fun() {

            }
            val error = fun() {
                Toast.makeText(requireActivity(), resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultReleases.message.toString())
            }
            observeDataProcessing(status = resultReleases.status, success = success, loading = loading, error = error)
        }
    }

    private fun observeTrackRecommendations() {
        homeViewModel.fetchRecommendations().observe(viewLifecycleOwner) { resultRecommendations ->
            val success = fun() {
                resultRecommendations.data?.body()?.let { trackRecommendations ->
                    trackNames.addAll(trackRecommendations.trackNames)
                    trackArtistNames.addAll(trackRecommendations.trackArtistNames)
                    trackPreviewUrls.addAll(trackRecommendations.trackPreviewUrls)
                    trackImageUrls.addAll(trackRecommendations.trackImageUrls)

                    trackRecommendations.tracks?.run {
                        trackRecommendationsAdapter.setData(this)
                        visibleTrackRecommendations()
                    }
                }
            }
            val loading = fun() {

            }
            val error = fun() {
                Toast.makeText(requireActivity(), resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultRecommendations.message.toString())
            }
            observeDataProcessing(status = resultRecommendations.status, success = success, loading = loading, error = error)
        }
    }

    private fun observeDataProcessing(status: Status, success: () -> Unit, loading: () -> Unit, error: () -> Unit) {
        when (status) {
            Status.SUCCESS -> success()
            Status.LOADING -> loading()
            Status.ERROR -> error()
        }
    }

    private fun foregroundViewActionDown(view: View) {
        view.foreground = AppCompatResources.getDrawable(view.context, R.drawable.foreground_rounded_action_down)
    }

    private fun animateViewActionUp(view: View) {
        val animator: ObjectAnimator = ObjectAnimator.ofInt(view.foreground, "alpha", 255, 0)
        animator.duration = 300
        animator.start()
    }

    private fun animateViewActionCancel(view: View) {
        val animator: ObjectAnimator = ObjectAnimator.ofInt(view.foreground, "alpha", 255, 0)
        animator.duration = 300
        animator.start()
    }

    private fun visibleFeaturedPlaylists() {
        llFeaturedPlaylists.visibility = View.VISIBLE
    }

    private fun visibleNewReleases() {
        tvNewReleases.visibility = View.VISIBLE
        rvNewReleases.visibility = View.VISIBLE
    }

    private fun visibleTrackRecommendations() {
        tvTrackRecommendations.visibility = View.VISIBLE
        rvTrackRecommendations.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}