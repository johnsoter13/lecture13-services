package edu.uw.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = "Main";

    private MusicService mService;
    private boolean mServiceBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //when "Start" button is pressed
    public void handleStart(View v){
        Log.i(TAG, "Start pressed");

        //on main thread
        //        for(int count=0; count<=10; count++){
        //            Log.v(TAG, "Count: "+count);
        //            try {
        //                Thread.sleep(2000); //sleep for 2 seconds
        //            } catch (InterruptedException e) {
        //                Thread.currentThread().interrupt();
        //            }
        //        }

        //using java thread
        //        Thread countThread = new Thread(new CountRunner());
        //        countThread.start();

        //using async task
        //        CountTask task = new CountTask();
        //        task.execute(1,5);

        //using service
        startService(new Intent(MainActivity.this, CountingService.class));
    }

    //when "Stop" button is pressed
    public void handleStop(View v){
        Log.i(TAG, "Stop pressed");

        stopService(new Intent(MainActivity.this, CountingService.class));
    }

    public class CountRunner implements Runnable {
        public void run() {
            for(int count=0; count<=10; count++){
                Log.v(TAG, "Count: "+count);
                try {
                    Thread.sleep(2000); //sleep for 2 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public class CountTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Start counting!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Integer... bounds) {
            for(int count=bounds[0]; count<=bounds[1]; count++){
                Log.v(TAG, "Count: "+count);
                publishProgress(count);
                try {
                    Thread.sleep(2000); //sleep for 2 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            return "All done!";
        }

        @Override
        protected void onProgressUpdate(Integer... count) {
            super.onProgressUpdate(count);
            Toast.makeText(MainActivity.this, "Count: " + count[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }


    /* Media controls */
    public void playMedia(View v){
        startService(new Intent(MainActivity.this, MusicService.class));
    }

    public void pauseMedia(View v){
        if(mServiceBound){
            mService.pauseMusic();
        }
    }

    public void stopMedia(View v){
        if(mServiceBound){
            mService.stopMusic();
        }
        //stopService(new Intent(MainActivity.this, MusicService.class));
    }


    @Override
    protected void onStart() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(mServiceBound){
            unbindService(this);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "Activity destroyed");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_quit:
                finish(); //end the Activity
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.v(TAG, "Service bound");
        MusicService.LocalBinder binder = (MusicService.LocalBinder) service; //cast to specific type
        ((TextView)findViewById(R.id.txtSongTitle)).setText(binder.getSongName());

        mService = binder.getService(); //save reference to the service
        mServiceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.v(TAG, "Service unbound");
        mServiceBound = false;
    }
}
