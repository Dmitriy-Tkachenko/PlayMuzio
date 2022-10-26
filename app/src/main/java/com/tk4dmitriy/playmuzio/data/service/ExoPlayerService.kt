package com.tk4dmitriy.playmuzio.data.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.tk4dmitriy.playmuzio.ui.activity.TrackPlayActivity
import com.tk4dmitriy.playmuzio.utils.TAG


private const val NOTIFICATION_ID = 101
private const val CHANNEL_ID = "SERVICE_PLAYER_CHANNEL"
private const val CHANNEL_NAME = "SERVICE_PLAYER"

class ExoPlayerService: Service(), Player.Listener {
    private val binder = ExoPlayerServiceBinder()

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerControlView: PlayerControlView
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var playerNotificationManager: PlayerNotificationManager

    private var trackPreviewUrls: ArrayList<String>? = null
    private var trackNames: ArrayList<String>? = null
    private var trackArtistNames: ArrayList<String>? = null
    private var trackImageUrls: ArrayList<String>? = null

    private var oldPosition = 0
    private var newPosition = 0

    override fun onBind(p0: Intent?): IBinder {
        Log.d(this@ExoPlayerService.TAG, "bind")
        return binder
    }

    override fun onRebind(intent: Intent?) {
        Log.d(this@ExoPlayerService.TAG, "rebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(this@ExoPlayerService.TAG, "unbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(this@ExoPlayerService.TAG, "create")
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSessionCompat = MediaSessionCompat(this, "Service")
        mediaSessionCompat.isActive = true
        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat).also {
            it.setQueueNavigator(
                object : TimelineQueueNavigator(mediaSessionCompat) {
                    override fun getMediaDescription(
                        player: Player,
                        windowIndex: Int
                    ): MediaDescriptionCompat {
                        val extras = Bundle().apply {
                            putString(MediaMetadataCompat.METADATA_KEY_TITLE, exoPlayer.currentMediaItem?.mediaMetadata?.title.toString())
                            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, exoPlayer.currentMediaItem?.mediaMetadata?.artist.toString())
                        }

                        Log.d(this@ExoPlayerService.TAG, exoPlayer.currentMediaItem?.mediaMetadata?.artworkUri?.normalizeScheme().toString())

                        return MediaDescriptionCompat.Builder()
                            .setIconUri(exoPlayer.currentMediaItem?.mediaMetadata?.artworkUri)
                            .setExtras(extras)
                            .build()
                    }
                })
            it.setPlayer(exoPlayer)
        }

        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayer.addListener(object: Player.Listener {
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == 0 || reason == 1) {
                    this@ExoPlayerService.newPosition = newPosition.mediaItemIndex
                    this@ExoPlayerService.oldPosition = oldPosition.mediaItemIndex
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Log.d(this@ExoPlayerService.TAG, reason.toString())
                if (mediaItem?.mediaId != "") {
                    val trackName = mediaItem?.mediaMetadata?.title.toString()
                    val trackArtist = mediaItem?.mediaMetadata?.artist.toString()
                    val trackImageUrl = mediaItem?.mediaMetadata?.artworkUri.toString()
                    CURRENT_TRACK_URL = mediaItem?.mediaId.toString()
                    updateTrackUI(trackName = trackName, trackArtist = trackArtist, trackImageUrl = trackImageUrl)
                    updateUISub()
                } else {
                    // Skip empty track
                    if (newPosition > oldPosition) exoPlayer.seekToNextMediaItem()
                    else exoPlayer.seekToPreviousMediaItem()
                }
            }
        })

        startNotification()

        IS_RUNNING = true
    }

    override fun onDestroy() {
        super.onDestroy()
        IS_RUNNING = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.run {
            if (this == PLAY_TRACK) {
                val trackPreviewUrlsIntent = intent.getStringArrayListExtra(TRACK_PREVIEW_URLS)
                val trackPosition = intent.getIntExtra(TRACK_POSITION, 0)

                if (trackPreviewUrls != trackPreviewUrlsIntent) {
                    trackPreviewUrls = trackPreviewUrlsIntent
                    trackNames = intent.getStringArrayListExtra(TRACK_NAMES)
                    trackArtistNames = intent.getStringArrayListExtra(TRACK_ARTISTS)
                    trackImageUrls = intent.getStringArrayListExtra(TRACK_IMAGE_URLS)

                    val mediaItems = getNewMediaItems()
                    exoPlayer.clearMediaItems()
                    exoPlayer.addMediaItems(mediaItems)
                }

                //Log.d(this@ExoPlayerService.TAG, "${exoPlayer.getMediaItemAt(trackPosition).mediaId} \n ${exoPlayer.currentMediaItem?.mediaId}")

                if (exoPlayer.getMediaItemAt(trackPosition).mediaId != exoPlayer.currentMediaItem?.mediaId || !exoPlayer.isCurrentMediaItemSeekable) {
                    exoPlayer.prepare()
                    exoPlayer.seekToDefaultPosition(trackPosition)
                    exoPlayer.play()

                    CURRENT_TRACK_URL = exoPlayer.currentMediaItem?.mediaId.toString()
                }
            }
        }
        return START_STICKY
    }

    private fun getNewMediaItems(): List<MediaItem> {
        val mediaItems: MutableList<MediaItem> = mutableListOf()
        trackPreviewUrls?.forEachIndexed { index, url ->
            val positionTrackImageUrl = getPositionImageUrl(index)
            val uri: Uri = Uri.parse(trackImageUrls!![positionTrackImageUrl])
            mediaItems.add(
                MediaItem.Builder()
                    .setUri(url)
                    .setMediaId(url)
                    .setMediaMetadata(
                        MediaMetadata.Builder().setTitle(trackNames!![index])
                            .setArtist(trackArtistNames!![index]).setTrackNumber(index)
                            .setArtworkUri(uri).build()
                    )
                    .build()
            )
        }
        return mediaItems
    }

    private fun getPositionImageUrl(position: Int): Int {
        return if (trackImageUrls?.size == 1) 0
        else position
    }

    fun attachPlayerControlView(playerControlView: PlayerControlView) {
        this.playerControlView = playerControlView
        this.playerControlView.player = exoPlayer
    }

    private fun updateTrackUI(trackName: String, trackArtist: String, trackImageUrl: String) {
        val intent = Intent()
        intent.action = UPDATE_TRACK_UI
        intent.putExtra(TRACK_NAME, trackName)
        intent.putExtra(TRACK_ARTIST, trackArtist)
        intent.putExtra(TRACK_IMAGE_URL, trackImageUrl)
        sendBroadcast(intent)
    }

    private fun updateUISub() {
        val intent = Intent()
        intent.action = UPDATE_UI
        sendBroadcast(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSessionCompat.release()
        exoPlayer.clearMediaItems()
        exoPlayer.stop()
        exoPlayer.release()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        }
        stopSelf()
    }

    private fun startNotification() {
        val adapter = object: PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                return player.mediaMetadata.title ?: ""
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val intent = Intent(this@ExoPlayerService, TrackPlayActivity::class.java)
                intent.action = START_FROM_NOTIFICATION
                intent.putExtra(TRACK_NAME, exoPlayer.currentMediaItem?.mediaMetadata?.title)
                intent.putExtra(TRACK_ARTIST, exoPlayer.currentMediaItem?.mediaMetadata?.artist)
                intent.putExtra(TRACK_IMAGE_URL, exoPlayer.currentMediaItem?.mediaMetadata?.artworkUri.toString())
                return PendingIntent.getActivity(this@ExoPlayerService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            override fun getCurrentContentText(player: Player): CharSequence {
                return player.mediaMetadata.artist ?: ""
            }

            override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                if (player.mediaMetadata.artworkUri == null) return null
                var image: Bitmap? = null
                Glide.with(this@ExoPlayerService)
                    .asBitmap()
                    .load(player.mediaMetadata.artworkUri)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            image = resource
                            callback.onBitmap(resource)
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
                return image
            }
        }
        val notification = object: PlayerNotificationManager.NotificationListener {
            private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                super.onNotificationCancelled(notificationId, dismissedByUser)
                mediaSessionCompat.release()
                exoPlayer.clearMediaItems()
                exoPlayer.stop()
                exoPlayer.release()
                stopForeground(true)
                stopSelf()
            }

            override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { setSound(null, null) }
                    notificationManager.createNotificationChannel(channel)
                }
                startForeground(notificationId, notification)
            }
        }
        playerNotificationManager = PlayerNotificationManager.Builder(applicationContext, NOTIFICATION_ID, CHANNEL_ID).setMediaDescriptionAdapter(adapter).setNotificationListener(notification).build().apply {
            setUseFastForwardAction(false)
            setUseRewindAction(false)
            setPlayer(exoPlayer)
        }
        playerNotificationManager.setMediaSessionToken(mediaSessionCompat.sessionToken)
    }

    inner class ExoPlayerServiceBinder: Binder() {
        val service: ExoPlayerService
            get() = this@ExoPlayerService
    }

    companion object {
        // action
        const val START_FROM_NOTIFICATION = "START_FROM_NOTIFICATION"
        const val PLAY_TRACK = "PLAY_TRACK"
        const val UPDATE_TRACK_UI = "UPDATE_TRACK_UI"
        const val UPDATE_UI = "UPDATE_UI"
        // extra
        const val TRACK_PREVIEW_URLS = "TRACK_PREVIEW_URLS"
        const val TRACK_POSITION = "TRACK_POSITION"
        const val TRACK_NAME = "TRACK_NAME"
        const val TRACK_NAMES = "TRACK_NAMES"
        const val TRACK_ARTIST = "TRACK_ARTIST"
        const val TRACK_ARTISTS = "TRACK_ARTISTS"
        const val TRACK_IMAGE_URL = "TRACK_IMAGE_URL"
        const val TRACK_IMAGE_URLS = "TRACK_IMAGE_URLS"
        // auxiliary
        var IS_RUNNING = false
        var CURRENT_TRACK_URL = ""
    }
}