package edu.uw.servicedemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * A service to play music in the background, as entertainment.
 */
public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "Music";

    private String songName = "The Entertainer";

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        Log.v(TAG, "Service started");
        super.onCreate();
    }

    private int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(this, R.raw.scott_joplin_the_entertainer_1902);
            //no need to call prepare(); create() does that for you!
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Music Player")
                .setContentText("Now playing: "+songName)
                .setContentIntent(pendingIntent)
                .setOngoing(true) //cannot be dismissed by the user
                .build();
        startForeground(NOTIFICATION_ID, notification); //make this a foreground service!

        mediaPlayer.start();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf(); //stop since we're done playing. This will call onDestroy to clean up
    }

    public void pauseMusic() {
        mediaPlayer.pause();
    }

    public void stopMusic() {
        stopForeground(true);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private final IBinder mLocalBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        public String getSongName() {
            return songName;
        }

        public MusicService getService() {
            // Return this instance of this Service so clients can call public methods on it!
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }


    @Override
    public void onDestroy() {
        stopMusic();
        super.onDestroy();
    }

}
