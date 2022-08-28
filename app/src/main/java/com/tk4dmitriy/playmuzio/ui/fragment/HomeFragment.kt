package com.tk4dmitriy.playmuzio.ui.fragment

import android.animation.ObjectAnimator
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.data.model.endpoints.trackRecommendations.Track
import com.tk4dmitriy.playmuzio.ui.activity.AlbumActivity
import com.tk4dmitriy.playmuzio.ui.adapter.BrowseCategoriesAdapter
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
    private lateinit var tvBrowseCategories: TextView
    private lateinit var rvFeaturedPlaylists: RecyclerView
    private lateinit var rvNewReleases: RecyclerView
    private lateinit var rvTrackRecommendations: RecyclerView
    private lateinit var rvBrowseCategories: RecyclerView
    private lateinit var featuredPlaylistsAdapter: HomeFeaturedPlaylistsAdapter
    private lateinit var newReleasesAdapter: NewReleasesAdapter
    private lateinit var trackRecommendationsAdapter: HomeTrackRecommendationsAdapter
    private lateinit var browseCategoriesAdapter: BrowseCategoriesAdapter

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(owner = this,
            ViewModelFactory(apiHelper = ApiHelper(RetrofitBuilder.apiService))
        )[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        featuredPlaylistsAdapter = HomeFeaturedPlaylistsAdapter()
        newReleasesAdapter = NewReleasesAdapter()
        trackRecommendationsAdapter = HomeTrackRecommendationsAdapter()
        browseCategoriesAdapter = BrowseCategoriesAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupUI(view = view)
        callbackProcessing()
        return view
    }

    private fun setupUI(view: View) {
        llFeaturedPlaylists = view.findViewById(R.id.ll_featured_playlists)
        etSearch = view.findViewById(R.id.et_search)

        tvNewReleases = view.findViewById(R.id.tv_new_releases)
        tvFeaturedPlaylists = view.findViewById(R.id.tv_featured_playlists)
        tvTrackRecommendations = view.findViewById(R.id.tv_may_like)
        tvBrowseCategories = view.findViewById(R.id.tv_browse_categories)

        rvFeaturedPlaylists = view.findViewById(R.id.rv_featured_playlists)
        rvNewReleases = view.findViewById(R.id.rv_new_releases)
        rvTrackRecommendations = view.findViewById(R.id.rv_track_recommendations)
        rvBrowseCategories = view.findViewById(R.id.rv_browse_categories)

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

        rvBrowseCategories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = browseCategoriesAdapter
        }
    }

    private fun callbackProcessing() {
        featuredPlaylistsAdapter.attachCallback(object: HomeFeaturedPlaylistsAdapter.Callback {
            override fun touchOnView(item: com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.Item, view: View, action: Int) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> foregroundViewActionDown(view = view)
                    MotionEvent.ACTION_UP -> animateViewActionUp(view = view)
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel(view = view)
                }
            }
        })
        newReleasesAdapter.attachCallback(object: NewReleasesAdapter.Callback {
            override fun touchOnView(item: com.tk4dmitriy.playmuzio.data.model.endpoints.newReleases.Item, view: View, action: Int) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> foregroundViewActionDown(view = view)
                    MotionEvent.ACTION_UP -> {
                        animateViewActionUp(view = view)
                        val intent = Intent(requireActivity(), AlbumActivity::class.java)
                        intent.putExtra("URL", item.href)
                        startActivity(intent)
                    }
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel(view = view)
                }
            }
        })
        trackRecommendationsAdapter.attachCallback(object: HomeTrackRecommendationsAdapter.Callback {
            override fun touchOnView(track: Track, view: View, action: Int) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> foregroundViewActionDown(view = view)
                    MotionEvent.ACTION_UP -> animateViewActionUp(view = view)
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel(view = view)
                }
            }
        })
        browseCategoriesAdapter.attachCallback(object: BrowseCategoriesAdapter.Callback {
            override fun touchOnView(item: com.tk4dmitriy.playmuzio.data.model.endpoints.browseCategories.Item, view: View, action: Int) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> foregroundViewActionDown(view = view)
                    MotionEvent.ACTION_UP -> animateViewActionUp(view = view)
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel(view = view)
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeFeaturedPlaylists()
        observeNewReleases()
        observeTrackRecommendations()
        observeBrowseCategories()
    }

    private fun observeFeaturedPlaylists() {
        homeViewModel.fetchFeaturedPlaylists().observe(viewLifecycleOwner) { resultPlaylists ->
            val success = fun() {
                resultPlaylists.data?.body()?.run {
                    val message = message
                    playlists?.run {
                        tvFeaturedPlaylists.apply {
                            text = message
                            isSelected = true
                        }
                        featuredPlaylistsAdapter.setData(items)
                        visibleFeaturedPlaylists()
                    }
                }
            }
            val loading = fun() {
            }
            val error = fun() {
                Toast.makeText(requireActivity(), resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultPlaylists.message.toString())
            }
            observeDataProcessing(status = resultPlaylists.status, success = success, loading = loading, error = error)
        }
    }

    private fun observeNewReleases() {
        homeViewModel.fetchNewReleases().observe(viewLifecycleOwner) { resultReleases ->
            val success = fun() {
                resultReleases.data?.body()?.albums?.run {
                    newReleasesAdapter.setData(items)
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
                resultRecommendations.data?.body()?.run {
                    trackRecommendationsAdapter.setData(tracks)
                    visibleTrackRecommendations()
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

    private fun observeBrowseCategories() {
        homeViewModel.fetchBrowseCategories().observe(viewLifecycleOwner) { resultCategories ->
            val success = fun() {
                resultCategories.data?.body()?.categories?.run {
                    browseCategoriesAdapter.setData(items)
                    visibleBrowseCategories()
                }
            }
            val loading = fun() {

            }
            val error = fun() {
                Toast.makeText(requireActivity(),resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultCategories.message.toString())
            }
            observeDataProcessing(status = resultCategories.status, success = success, loading = loading, error = error)
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

    private fun visibleBrowseCategories() {
        tvBrowseCategories.visibility = View.VISIBLE
        rvBrowseCategories.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}