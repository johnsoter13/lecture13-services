package edu.uw.servicedemo

import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val TAG = "Main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //when "Start Count" button is pressed
    fun handleStartCount(v: View) {
        Log.i(TAG, "Start pressed")

        //on main thread
        for(count in 0..10){
            Log.v(TAG, "Count: $count");
            try {
                Thread.sleep(2000); //sleep for 2 seconds
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }


        //using java thread
//        val countThread = Thread(CountRunner());
//        countThread.start();

        //using async task
//        val task = CountTask();
//        task.execute(1, 5);

        //using service
        //TODO
    }

    //when "StopCount" button is pressed
    fun handleStopCount(v: View) {
        Log.i(TAG, "Stop pressed")
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



    /******************
     * Media controls *
     ******************/

    private var mediaPlayer: MediaPlayer? = null

    fun playMedia(v: View) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.scott_joplin_the_entertainer_1902)
        }
        mediaPlayer!!.start()
    }

    fun pauseMedia(v: View) {
        mediaPlayer!!.pause()
    }

    fun stopMedia(v: View) {
        if(mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
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
