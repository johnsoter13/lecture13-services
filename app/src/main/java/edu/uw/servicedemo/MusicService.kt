package edu.uw.servicedemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

/**
 * A service to play music in the background, as entertainment.
 */
class MusicService : Service(), MediaPlayer.OnCompletionListener {

    private val NOTIFICATION_CHANNEL_ID = "my_channel_01" //channel ID
    private val TAG = "Music"

    private val songName = "The Entertainer"

    private var mediaPlayer: MediaPlayer? = null

    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        Log.v(TAG, "Service started")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.scott_joplin_the_entertainer_1902)
            //no need to call prepare(); create() does that for you!
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0,
                Intent(applicationContext, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Demo channel", NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Music Player")
                .setContentText("Now playing: $songName")
                .setContentIntent(pendingIntent)
                .setOngoing(true) //cannot be dismissed by the user
                .build()

        startForeground(NOTIFICATION_ID, notification) //make this a foreground service!

        mediaPlayer!!.start()

        // what service should do if it gets destroyed
        // When the service is destroyed it should not be recreated
        return Service.START_NOT_STICKY
    }

    override fun onCompletion(mp: MediaPlayer) {
        stopSelf() //stop since we're done playing. This will call onDestroy to clean up
    }

    fun pauseMusic() {
        mediaPlayer!!.pause()
    }

    fun stopMusic() {
        stopForeground(true)
        if(mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    inner class LocalBinder : Binder() {

        fun getSongName():String {
            return songName
        }

        // Return this instance of this Service so clients can call public methods on it!
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    private val mLocalBinder = LocalBinder()


    override fun onBind(intent: Intent): IBinder? {
        return mLocalBinder
    }


    override fun onDestroy() {
        stopMusic()
        super.onDestroy()
    }
}
