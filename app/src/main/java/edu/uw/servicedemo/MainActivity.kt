package edu.uw.servicedemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.AsyncTask
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), ServiceConnection {

    private val TAG = "Main"

    private lateinit var mService: MusicService
    private var mServiceBound: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //when "Start" button is pressed
    fun handleStart(v: View) {
        Log.i(TAG, "Start pressed")

        //on main thread
//        for(count in 0..10){
//            Log.v(TAG, "Count: $count");
//            try {
//                Thread.sleep(2000); //sleep for 2 seconds
//            } catch (e: InterruptedException) {
//                Thread.currentThread().interrupt();
//            }
//        }


        //using java thread
//        val countThread = Thread(CountRunner());
//        countThread.start();

        //using async task
//        val task = CountTask();
//        task.execute(1, 5);

        //using service
        startService(Intent(this@MainActivity, CountingService::class.java))
    }

    //when "Stop" button is pressed
    fun handleStop(v: View) {
        Log.i(TAG, "Stop pressed")

        stopService(Intent(this@MainActivity, CountingService::class.java))
    }

    //Thread runner version
    inner class CountRunner : Runnable {
        override fun run() {
            for (count in 0..10) {
                Log.v(TAG, "Count: $count")
                try {
                    Thread.sleep(2000) //sleep for 2 seconds
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }

            }
        }
    }

    //AsyncTask version
    inner class CountTask : AsyncTask<Int, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(this@MainActivity, "Start counting!", Toast.LENGTH_SHORT).show()
        }

        override fun doInBackground(vararg bounds: Int?): String {
            for (count in bounds[0]!!..bounds[1]!!) {
                Log.v(TAG, "Count: $count")
                publishProgress(count)
                try {
                    Thread.sleep(2000) //sleep for 2 seconds
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }

            return "All done!"
        }

        override fun onProgressUpdate(vararg count: Int?) {
            super.onProgressUpdate(*count) //spread operator
            Toast.makeText(this@MainActivity, "Count: ${count[0]}", Toast.LENGTH_SHORT).show()
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
        }
    }



    /* Media controls */
    fun playMedia(v: View) {
        startService(Intent(this, MusicService::class.java))
    }

    fun pauseMedia(v: View) {
        if (mServiceBound) {
            mService.pauseMusic()
        }
    }

    fun stopMedia(v: View) {
        if (mServiceBound) {
            mService.stopMusic()
        }
        //stopService(new Intent(MainActivity.this, MusicService.class));
    }


    override fun onStart() {
        val intent = Intent(this@MainActivity, MusicService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        super.onStart()
    }

    override fun onStop() {
        if (mServiceBound) {
            unbindService(this)
        }
        super.onStop()
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Log.v(TAG, "Service bound")
        val binder = service as MusicService.LocalBinder //cast to specific type
        (findViewById<View>(R.id.txtSongTitle) as TextView).text = binder.getSongName()

        mService = binder.getService() //save reference to the service
        mServiceBound = true
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Log.v(TAG, "Service unbound")
        mServiceBound = false
    }

    override fun onDestroy() {
        Log.v(TAG, "Activity destroyed")
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_quit -> {
                finish() //end the Activity
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
