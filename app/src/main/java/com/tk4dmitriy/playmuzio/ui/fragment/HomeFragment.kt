package com.tk4dmitriy.playmuzio.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.data.model.endpoints.featuredPlaylists.Item
import com.tk4dmitriy.playmuzio.ui.adapter.Callback
import com.tk4dmitriy.playmuzio.ui.adapter.HomeFeaturedPlaylistsAdapter
import com.tk4dmitriy.playmuzio.ui.adapter.NewReleasesAdapter
import com.tk4dmitriy.playmuzio.ui.viewmodel.HomeViewModel
import com.tk4dmitriy.playmuzio.ui.viewmodel.ViewModelFactory
import com.tk4dmitriy.playmuzio.utils.Constants
import com.tk4dmitriy.playmuzio.utils.TAG
import com.tkachenko.playmusic.utils.Status


class HomeFragment: Fragment() {
    private lateinit var llFeaturedPlaylists: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var tvFeaturedPlaylists: TextView
    private lateinit var tvNewReleases: TextView
    private lateinit var rvFeaturedPlaylists: RecyclerView
    private lateinit var rvNewReleases: RecyclerView
    private lateinit var featuredPlaylistsAdapter: HomeFeaturedPlaylistsAdapter
    private lateinit var newReleasesAdapter: NewReleasesAdapter

    private var etSearchHasFocused: Boolean = false

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(owner = this,
            ViewModelFactory(apiHelper = ApiHelper(RetrofitBuilder.apiService))
        )[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        featuredPlaylistsAdapter = HomeFeaturedPlaylistsAdapter()
        newReleasesAdapter = NewReleasesAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupUI(view = view)
        callbackProcessing()
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI(view: View) {
        llFeaturedPlaylists = view.findViewById(R.id.ll_featured_playlists)
        etSearch = view.findViewById(R.id.et_search)

        tvNewReleases = view.findViewById(R.id.tv_new_releases)
        tvFeaturedPlaylists = view.findViewById(R.id.tv_featured_playlists)
        rvFeaturedPlaylists = view.findViewById(R.id.rv_featured_playlists)
        rvNewReleases = view.findViewById(R.id.rv_new_releases)

        rvFeaturedPlaylists.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = featuredPlaylistsAdapter
        }

        rvNewReleases.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newReleasesAdapter
        }
    }

    private fun callbackProcessing() {
        featuredPlaylistsAdapter.attachCallback(object: Callback {
            override fun touchOnView(item: Item, view: View, action: Int) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> foregroundViewActionDown(view = view)
                    MotionEvent.ACTION_UP -> animateViewActionUp(view = view)
                    MotionEvent.ACTION_CANCEL -> animateViewActionCancel(view = view)
                }
            }
        })
    }

    private fun foregroundViewActionDown(view: View) {
        view.foreground = AppCompatResources.getDrawable(view.context, R.drawable.foreground_action_down)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.fetchCurrentUsersProfile().observe(requireActivity()) { resultProfile ->
            when (resultProfile.status) {
                Status.SUCCESS -> {
                    val country = resultProfile.data?.body()?.country
                    if (country == "RU" || country == "" || country == null) Constants.COUNTRY = "US"
                    else Constants.COUNTRY = country

                    observeFeaturedPlaylists()
                    observeNewReleases()
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity(), "${resources.getString(R.string.error)}: ${resultProfile.message}", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, resultProfile.message.toString())
                }
            }
        }
    }

    private fun observeFeaturedPlaylists() {
        homeViewModel.fetchFeaturedPlaylists(Constants.COUNTRY).observe(requireActivity()) { resultPlaylists ->
            when (resultPlaylists.status) {
                Status.SUCCESS -> {
                    val message = resultPlaylists.data?.body()?.message
                    val items = resultPlaylists.data?.body()?.playlists?.items

                    message?.run {
                        tvFeaturedPlaylists.apply {
                            text = this@run
                            isSelected = true
                        }
                    }

                    items?.run {
                        featuredPlaylistsAdapter.setData(this)
                        visibleFeaturedPlaylists()
                    }
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity(),"${resources.getString(R.string.error)}: ${resultPlaylists.message}", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, resultPlaylists.message.toString())
                }
            }
        }
    }

    private fun observeNewReleases() {
        homeViewModel.fetchNewReleases(country = Constants.COUNTRY).observe(requireActivity()) { resultReleases ->
            when (resultReleases.status) {
                Status.SUCCESS -> {
                    val items = resultReleases.data?.body()?.albums?.items
                    items?.run {
                        newReleasesAdapter.setData(this)
                        visibleNewReleases()
                    }
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    Toast.makeText(requireActivity(), "${resources.getString(R.string.error)}: ${resultReleases.message}", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, resultReleases.message.toString())
                }
            }
        }
    }

    private fun visibleFeaturedPlaylists() {
        llFeaturedPlaylists.visibility = View.VISIBLE
    }

    private fun visibleNewReleases() {
        tvNewReleases.visibility = View.VISIBLE
        rvNewReleases.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}