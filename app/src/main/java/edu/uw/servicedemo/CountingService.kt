package edu.uw.servicedemo

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast

/**
 * A service to count
 */
class CountingService : IntentService("CountingService") {

    private val TAG = "CountingService"

    private var count: Int = 0
    private lateinit var mHandler: Handler

    override fun onCreate() {
        Log.v(TAG, "Service started")
        mHandler = Handler()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "Intent received")
        Toast.makeText(this, "Intent received", Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.v(TAG, "Handling Intent")

        count = 1
        while (count <= 10) {
            Log.v(TAG, "Count: $count")
            mHandler.post {
                Toast.makeText(this@CountingService, "Count: $count", Toast.LENGTH_SHORT).show()
                Log.v(TAG, "" + count)
            }

            if (count == 5) {
                stopSelf()
            }

            try {
                Thread.sleep(2000) //sleep for 2 seconds
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            count++
        }
    }

    override fun onDestroy() {
        Log.v(TAG, "Service finished")
        count = 11 //stop counting
        super.onDestroy()
    }
}
