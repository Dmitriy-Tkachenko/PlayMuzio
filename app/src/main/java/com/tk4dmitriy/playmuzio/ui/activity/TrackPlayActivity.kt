package com.tk4dmitriy.playmuzio.ui.activity

import android.R.attr.button
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.*
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.View.OnLayoutChangeListener
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.setMargins
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.tk4dmitriy.playmuzio.R
import com.tk4dmitriy.playmuzio.data.api.ApiHelper
import com.tk4dmitriy.playmuzio.data.api.RetrofitBuilder
import com.tk4dmitriy.playmuzio.data.service.ExoPlayerService
import com.tk4dmitriy.playmuzio.ui.viewmodel.TrackViewModel
import com.tk4dmitriy.playmuzio.ui.viewmodel.ViewModelFactory
import com.tk4dmitriy.playmuzio.utils.Status
import com.tk4dmitriy.playmuzio.utils.TAG
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlin.math.abs
import kotlin.math.roundToInt


class TrackPlayActivity: AppCompatActivity() {
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolBarLayout: Toolbar
    private lateinit var ivBackground: ImageView
    private lateinit var sivTrackImage: ShapeableImageView
    private lateinit var llTrackDetails: LinearLayout
    private lateinit var tvToolbar: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvTrackName: TextView
    private lateinit var tvTrackLyrics: TextView
    private lateinit var playerControlView: PlayerControlView
    private lateinit var coordinatorLayout: CoordinatorLayout

    private var collapseState: Pair<Int, Int>? = null
    private var expandedTrackImageSize = 0F
    private var collapsedTrackImageSize = 0F
    private var expandedArtistNameSize = 0F
    private var collapsedArtistNameSize = 0F
    private var expandedTrackNameSize = 0F
    private var collapsedTrackNameSize = 0F
    private var startTrackImageAnimatePointY = 0F
    private var animateWeight: Float = 0F
    private var margin: Float = 0F
    private val upperLimitTransparently = ABROAD * 0.75
    private var isCalculated = false

    private var trackName: String = ""
    private var trackArtist: String = ""
    private var trackImageUrl: String = ""

    private val trackViewModel: TrackViewModel by lazy {
        ViewModelProvider(owner = this,
            ViewModelFactory(apiHelper = ApiHelper(RetrofitBuilder.get().apiService))
        )[TrackViewModel::class.java]
    }

    private var isBound = false
    private var serviceIntent: Intent? = null
    private var exoPlayerService: ExoPlayerService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            exoPlayerService = (iBinder as ExoPlayerService.ExoPlayerServiceBinder).service
            exoPlayerService?.attachPlayerControlView(playerControlView)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    private var isReceiver = false
    private var intentFilter: IntentFilter? = null
    private var broadcastReceiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ExoPlayerService.UPDATE_TRACK_UI) {
                trackName = intent.getStringExtra(ExoPlayerService.TRACK_NAME) ?: ""
                trackArtist = intent.getStringExtra(ExoPlayerService.TRACK_ARTIST) ?: ""
                trackImageUrl = intent.getStringExtra(ExoPlayerService.TRACK_IMAGE_URL) ?: ""
                observeTrack(track = trackName, artist = trackArtist)
                updateUi(trackName = trackName, trackArtist = trackArtist, trackImageUrl = trackImageUrl)
            }
            isReceiver = true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        trackName = intent.getStringExtra(TRACK_NAME) ?: ""
        trackArtist = intent.getStringExtra(TRACK_ARTIST) ?: ""
        trackImageUrl = intent.getStringExtra(TRACK_IMAGE_URL) ?: ""
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TRACK_NAME, trackName)
        outState.putString(TRACK_ARTIST, trackArtist)
        outState.putString(TRACK_IMAGE_URL, trackImageUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_play)
        setupUI()

        Log.d(this@TrackPlayActivity.TAG, "onCreate")

        serviceIntent = Intent(this@TrackPlayActivity, ExoPlayerService::class.java)
        intentFilter = IntentFilter()
        intentFilter!!.addAction(ExoPlayerService.UPDATE_TRACK_UI)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        registerReceiver(broadcastReceiver, intentFilter)

        if (savedInstanceState != null) {
            trackName = savedInstanceState.getString(TRACK_NAME) ?: ""
            trackArtist = savedInstanceState.getString(TRACK_ARTIST) ?: ""
            trackImageUrl = savedInstanceState.getString(TRACK_IMAGE_URL) ?: ""

        } else if (intent.action != null) {
            if (intent.action == START_FROM_RECOMMENDATIONS || intent.action == START_FROM_PLAYLIST || intent.action == START_FROM_ALBUM) {
                val trackPreviewUrls = intent.getStringArrayListExtra(TRACK_PREVIEW_URLS) ?: arrayListOf()
                val trackNames = intent.getStringArrayListExtra(TRACK_NAMES) ?: arrayListOf()
                val trackArtists = intent.getStringArrayListExtra(TRACK_ARTISTS) ?: arrayListOf()
                val trackImageUrls = intent.getStringArrayListExtra(TRACK_IMAGE_URLS) ?: arrayListOf()
                val trackPosition = intent.getIntExtra(TRACK_POSITION, 0)

                trackName = trackNames[trackPosition]
                trackArtist = trackArtists[trackPosition]
                trackImageUrl =
                    if (trackImageUrls.size == 1) trackImageUrls[0]
                    else trackImageUrls[trackPosition]

                startService(trackPreviewUrls, trackNames, trackArtists, trackImageUrls, trackPosition)
            } else if (intent.action == START_FROM_NOTIFICATION) {
                trackName = intent.getStringExtra(TRACK_NAME) ?: ""
                trackArtist = intent.getStringExtra(TRACK_ARTIST) ?: ""
                trackImageUrl = intent.getStringExtra(TRACK_IMAGE_URL) ?: ""
            }
        }

        observeTrack(track = trackName, artist = trackArtist)
        updateUi(trackName, trackArtist, trackImageUrl)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_track, menu)
        if (collapseState?.first == TO_COLLAPSED_STATE) {
            for (i in 0 until menu.size()) menu.getItem(i).isVisible = false
        }

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            isBound = false
            unbindService(serviceConnection)
        }
        if (isReceiver) {
            isReceiver = false
            unregisterReceiver(broadcastReceiver)
        }
    }

    private fun setupUI() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        appBarLayout = findViewById(R.id.app_bar_layout)
        toolBarLayout = findViewById(R.id.toolbar_layout)
        ivBackground = findViewById(R.id.iv_background)
        sivTrackImage = findViewById(R.id.siv_track_image)
        llTrackDetails = findViewById(R.id.ll_track_details)
        tvToolbar = findViewById(R.id.tv_toolbar)
        tvArtistName = findViewById(R.id.tv_artist_name)
        tvTrackName = findViewById(R.id.tv_track_name)
        tvTrackLyrics = findViewById(R.id.tv_track_lyrics)
        tvToolbar.isSelected = true
        tvArtistName.isSelected = true
        tvTrackName.isSelected = true
        playerControlView = findViewById(R.id.player_control)
        coordinatorLayout = findViewById(R.id.coordinator_layout)

        sivTrackImage.setOnClickListener {
            openOptionsMenu()
        }

        calculateAnimation()
        deleteExtraPaddingPortrait()
        setSupportActionBar(toolBarLayout)
    }

    private fun deleteExtraPaddingPortrait() {
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            (toolBarLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            (sivTrackImage.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top / 2
            (tvToolbar.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun observeTrack(track: String, artist: String) {
        trackViewModel.fetchTrackLyrics(track = track, artist = artist).observe(this) { resultLyrics ->
            val success = fun() {
                val lyrics = resultLyrics.data?.body()?.message?.body?.lyrics
                tvTrackLyrics.text = lyrics?.lyricsBody
            }
            val loading = fun() {

            }
            val error = fun() {
                Toast.makeText(this, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
                Log.d(TAG, resultLyrics?.message.toString())
            }
            observeDataProcessing(resultLyrics.status, success = success, loading = loading, error = error)
        }
    }

    private fun observeDataProcessing(status: Status, success: () -> Unit, loading: () -> Unit, error: () -> Unit) {
        when (status) {
            Status.SUCCESS -> success()
            Status.LOADING -> loading()
            Status.ERROR -> error()
        }
    }

    private fun updateUi(trackName: String, trackArtist: String, trackImageUrl: String) {
        tvArtistName.text = trackArtist
        tvTrackName.text = trackName
        tvToolbar.text = trackName

        if (!this.isFinishing && !this.isDestroyed) {
            Glide.with(this@TrackPlayActivity)
                .load(trackImageUrl)
                .apply(RequestOptions().override(SIZE_ORIGINAL))
                .into(sivTrackImage)
            Glide.with(this@TrackPlayActivity)
                .load(trackImageUrl)
                .transform(BlurTransformation(50, 10))
                .into(ivBackground)
        }
    }

    private fun startService(trackPreviewUrls: ArrayList<String>, trackNames: ArrayList<String>, trackArtistNames: ArrayList<String>, trackImageUrls: ArrayList<String>, trackPosition: Int) {
        serviceIntent?.action = ExoPlayerService.PLAY_TRACK
        serviceIntent?.putStringArrayListExtra(ExoPlayerService.TRACK_PREVIEW_URLS, trackPreviewUrls)
        serviceIntent?.putStringArrayListExtra(ExoPlayerService.TRACK_NAMES, trackNames)
        serviceIntent?.putStringArrayListExtra(ExoPlayerService.TRACK_ARTISTS, trackArtistNames)
        serviceIntent?.putStringArrayListExtra(ExoPlayerService.TRACK_IMAGE_URLS, trackImageUrls)
        serviceIntent?.putExtra(ExoPlayerService.TRACK_POSITION, trackPosition)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun calculateAnimation() {
        appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, currentScroll ->
                if (isCalculated.not()) {
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        expandedTrackImageSize = resources.getDimension(R.dimen.expand_track_image_size)
                        collapsedTrackImageSize = resources.getDimension(R.dimen.collapsed_image_size)
                        expandedArtistNameSize = resources.getDimension(R.dimen.expanded_artist_name_size)
                        collapsedArtistNameSize = resources.getDimension(R.dimen.collapsed_artist_name_size)
                        expandedTrackNameSize = resources.getDimension(R.dimen.expanded_track_name_size)
                        collapsedTrackNameSize = resources.getDimension(R.dimen.collapsed_track_name_size)
                    } else {
                        expandedTrackImageSize = resources.getDimension(R.dimen.expand_track_image_size_land)
                        collapsedTrackImageSize = resources.getDimension(R.dimen.collapsed_image_size)
                        expandedArtistNameSize = resources.getDimension(R.dimen.expanded_artist_name_size_land)
                        collapsedArtistNameSize = resources.getDimension(R.dimen.collapsed_artist_name_size)
                        expandedTrackNameSize = resources.getDimension(R.dimen.expanded_track_name_size_land)
                        collapsedTrackNameSize = resources.getDimension(R.dimen.collapsed_track_name_size)
                    }
                    margin = resources.getDimension(R.dimen.collapsed_track_image_margin)
                    startTrackImageAnimatePointY = 0F
                    animateWeight = 1 / (1 - startTrackImageAnimatePointY)
                    isCalculated = true
                }

                val offset = abs(currentScroll / appBarLayout.totalScrollRange.toFloat())
                updateViews(percentOffset = offset)
            }
        )
    }

    private fun updateViews(percentOffset: Float) {
        Log.d(this@TrackPlayActivity.TAG, percentOffset.toString())
        when {
            percentOffset > upperLimitTransparently -> llTrackDetails.visibility = View.GONE
            percentOffset < upperLimitTransparently -> llTrackDetails.visibility = View.VISIBLE
        }

        val result: Pair<Int, Int> =
            when {
                (percentOffset < ABROAD) -> Pair(TO_EXPANDED_STATE, collapseState?.second ?: WAIT_FOR_SWITCH)
                else -> Pair(TO_COLLAPSED_STATE, collapseState?.second ?: WAIT_FOR_SWITCH)
            }

        result.apply {
            collapseState = if (collapseState != this && collapseState != null) {
                when (first) {
                    TO_EXPANDED_STATE -> toExpandedState()
                    TO_COLLAPSED_STATE -> toCollapsedState()
                }
                Pair(first, SWITCHED)
            } else {
                if (percentOffset > startTrackImageAnimatePointY && percentOffset <= 1.0f) {
                    changeTrackImageSize(percentOffset = percentOffset)
                    changeTrackName(percentOffset = percentOffset)
                    changeArtistName(percentOffset = percentOffset)
                }
                Pair(first, WAIT_FOR_SWITCH)
            }
        }
    }

    private fun toExpandedState() {
        tvToolbar.visibility = View.INVISIBLE
        translationXTrackImageToExpanded()
        translationXtvToolbarToExpanded()
        invalidateOptionsMenu()
    }

    private fun toCollapsedState() {
        val translationX = calculateTranslationXTrackImage()
        translationXTrackImageToCollapsed(translationX)
        translationXtvToolbarToCollapsed()
        invalidateOptionsMenu()
    }

    private fun changeTrackImageSize(percentOffset: Float) {
        sivTrackImage.post {
            sivTrackImage.apply {
                val animateOffset = (percentOffset - startTrackImageAnimatePointY) * animateWeight
                val imageSize = expandedTrackImageSize - (expandedTrackImageSize - collapsedTrackImageSize) * animateOffset

                layoutParams.also {
                    if (it.height != imageSize.roundToInt()) {
                        it.height = imageSize.roundToInt()
                        it.width = imageSize.roundToInt()
                        requestLayout()
                    }
                }
            }
        }
    }

    private fun changeTrackName(percentOffset: Float) {
        tvTrackName.post {
            tvTrackName.apply {
                val animateOffset = (percentOffset - startTrackImageAnimatePointY) * 1
                val textSize = expandedTrackNameSize - (expandedTrackNameSize - collapsedTrackNameSize) * animateOffset
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                paint.isSubpixelText = true
                paint.isLinearText = true
            }
        }
    }

    private fun changeArtistName(percentOffset: Float) {
        tvArtistName.post {
            tvArtistName.apply {
                val animateOffset = (percentOffset - startTrackImageAnimatePointY) * 1
                val textSize = expandedArtistNameSize - (expandedArtistNameSize - collapsedArtistNameSize) * animateOffset
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                paint.isSubpixelText = true
                paint.isLinearText = true
            }
        }
    }

    private fun translationXTrackImageToExpanded() {
        ValueAnimator.ofFloat(sivTrackImage.translationX, 0f).apply {
            addUpdateListener {
                sivTrackImage.translationX = it.animatedValue as Float
            }
            start()
        }
    }

    private fun calculateTranslationXTrackImage() = appBarLayout.width / 2f - collapsedTrackImageSize / 2 - margin * 2

    private fun translationXTrackImageToCollapsed(translationX: Float) {
        ValueAnimator.ofFloat(sivTrackImage.translationX, translationX).apply {
            addUpdateListener {
                if (collapseState!!.first == TO_COLLAPSED_STATE) {
                    sivTrackImage.translationX = it.animatedValue as Float
                }
            }
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
    }

    private fun translationXtvToolbarToExpanded() {
        tvToolbar.apply {
            visibility = View.VISIBLE
            animate().translationY(-height.toFloat() - 50f).interpolator = AnticipateOvershootInterpolator()
        }
    }

    private fun translationXtvToolbarToCollapsed() {
        tvToolbar.apply {
            visibility = View.VISIBLE
            translationY = -height.toFloat() - 50f
            animate().translationY(0f).interpolator = AnticipateOvershootInterpolator()
        }
    }

    companion object {
        // action
        const val START_FROM_ALBUM = "START_FROM_ALBUM"
        const val START_FROM_PLAYLIST = "START_FROM_PLAYLIST"
        const val START_FROM_RECOMMENDATIONS = "START_FROM_RECOMMENDATIONS"
        const val START_FROM_NOTIFICATION = "START_FROM_NOTIFICATION"

        // extra
        const val TRACK_PREVIEW_URLS = "TRACK_PREVIEW_URLS"
        const val TRACK_POSITION = "TRACK_POSITION"
        const val TRACK_NAME = "TRACK_NAME"
        const val TRACK_NAMES = "TRACK_NAMES"
        const val TRACK_ARTIST = "TRACK_ARTIST"
        const val TRACK_ARTISTS = "TRACK_ARTISTS"
        const val TRACK_IMAGE_URL = "TRACK_IMAGE_URL"
        const val TRACK_IMAGE_URLS = "TRACK_IMAGE_URLS"

        private const val ABROAD = 1F
        private const val TO_EXPANDED_STATE = 0
        private const val TO_COLLAPSED_STATE = 1
        private const val WAIT_FOR_SWITCH = 0
        private const val SWITCHED = 1
    }
}